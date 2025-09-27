package com.example.cv_analyzer_api;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;
import java.util.Collections;
import java.util.Map;
import java.util.ArrayList; // Make sure to add this import at the top
import java.util.List;      // Make sure to add this import at the top

@Service
public class GeminiVisionService {

    private final RestClient restClient;
    private final ObjectMapper objectMapper;

    @Value("${gemini.api.key}")
    private String apiKey;

    private String apiModel = "gemini-2.5-flash";
    
    private String apiBaseUrl = "https://generativelanguage.googleapis.com/v1beta/models";

    // Constructor injection for RestClient and ObjectMapper
    public GeminiVisionService(RestClient.Builder restClientBuilder, ObjectMapper objectMapper) {
        this.restClient = restClientBuilder.build();
        this.objectMapper = objectMapper;
    }

    public CVData analyzeCv(MultipartFile imageFile) throws IOException {
        if (imageFile == null || imageFile.isEmpty()) {
            throw new IllegalArgumentException("No image file provided for analysis.");
        }

        // 1. Convert image to Base64
        byte[] imageBytes = imageFile.getBytes();
        String imageBase64 = Base64.getEncoder().encodeToString(imageBytes);

        // 2. Build the precise request payload
        GeminiRequest request = buildGeminiRequest(imageFile.getContentType(), imageBase64);

        // 3. Make the API call
        String apiUrl = String.format("%s/%s:generateContent", apiBaseUrl, apiModel);

        String responseBody;
        try {
            responseBody = restClient.post()
                    .uri(apiUrl) // Use the URL without the API key
                    .header("x-goog-api-key", this.apiKey) // Add the API key as a header
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(request)
                    .retrieve()
                    .body(String.class);
        } catch (Exception e) {
            // Log the actual error for debugging
            System.err.println("Error calling Gemini API: " +apiUrl+"\n"+ e.getMessage());
            throw new RuntimeException("An error occurred while communicating with the AI service.", e);
        }

        // 4. Parse the nested JSON response
        try {
            GeminiResponse geminiResponse = objectMapper.readValue(responseBody, GeminiResponse.class);
            
            // Extract the function call arguments which contain the CV data as a Map
            Map<String, Object> functionArgs = geminiResponse.getCandidates().get(0)
                    .getContent().getParts().get(0).getFunctionCall().getArgs();

            // Convert the Map back to a JSON string, then parse it into our CVData object
            String cvDataJson = objectMapper.writeValueAsString(functionArgs);
            return objectMapper.readValue(cvDataJson, CVData.class);
            
        } catch (JsonProcessingException | NullPointerException e) {
            System.err.println("Failed to parse JSON response from Gemini: " + e.getMessage());
            System.err.println("Raw AI response: " + responseBody);
            throw new RuntimeException("AI failed to return valid JSON.", e);
        }
    }



    private GeminiRequest buildGeminiRequest(String mimeType, String imageBase64) {
        // --- CONSOLIDATED CONTENT ---
        // We will now create a single list of "parts" for the main content.
        // This is the most standard way to send a multimodal request.

        // Part 1: The System Prompt as a text part.
        Part textPart = new Part(getSystemPrompt());

        // Part 2: The Image as an inline data part.
        InlineData inlineData = new InlineData(mimeType, imageBase64);
        Part imagePart = new Part(inlineData);
        
        // Create a list containing both the text instructions and the image.
        List<Part> allParts = new ArrayList<>();
        allParts.add(textPart);
        allParts.add(imagePart);
        
        Content combinedContent = new Content(allParts);

        // --- TOOL DEFINITION (This part remains the same) ---
        Object schema = loadSchemaAsObject();
        FunctionDeclaration functionDeclaration = new FunctionDeclaration("extract_cv_data", "Extracts structured data from a CV.", schema);
        Tool tool = new Tool(Collections.singletonList(functionDeclaration));
        
        // --- FINAL REQUEST ---
        // We pass null for systemInstruction because we've moved the prompt into the main content.
        return new GeminiRequest(
            Collections.singletonList(combinedContent),
            null, 
            Collections.singletonList(tool),
            null // Pass null for systemInstruction
        );
    }
    
    private String getSystemPrompt() {
        return "You are an expert HR assistant specializing in parsing CV and resume documents."
        		+ " I will provide you with one or more images of a CV's pages. "
        		+ "Your task is to analyze the content and structure of the CV and "
        		+ "extract the information into a structured JSON format according to the provided schema. "
        		+ "The responsibilities for experience and description for projects should be concise bullet points. "
        		+ "Pay special attention to skills, and group them into relevant categories like 'Languages', '"
        		+ "Frameworks & Libraries', 'Databases', and 'Tools'. Ensure project titles are correctly capitalized. "
        		+ "If a field is not present in the CV, omit it or use an empty string/array. "
        		+ "Also look for other non-standard sections like 'Awards', or 'Publications' "
        		+ "and extract them into the customSections array, using the section's heading "
        		+ "as the 'title' and its content as the 'text'.";
    }

    private Object loadSchemaAsObject() {
        try (InputStream inputStream = getClass().getResourceAsStream("/schema.json")) {
            if (inputStream == null) {
                throw new RuntimeException("Cannot find schema.json in resources.");
            }
            // Read the file and parse it into a generic Map (Object)
            return objectMapper.readValue(inputStream, Object.class);
        } catch (IOException e) {
            throw new RuntimeException("Failed to read or parse schema.json", e);
        }
    }
}