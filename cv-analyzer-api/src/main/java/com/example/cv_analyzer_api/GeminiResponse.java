package com.example.cv_analyzer_api;

import java.util.List;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

// This class represents the overall response from the Gemini API
@Data
public class GeminiResponse {
    private List<Candidate> candidates;

    
	@Data
    public static class Candidate {
        private Content content;
    }

    @Data
    public static class Content {
        private List<Part> parts;
        private String role;
    }

    @Data
    public static class Part {
        private FunctionCall functionCall;
    }

    @Data
    public static class FunctionCall {
        private String name;
        private Map<String, Object> args; // This will hold the JSON with our CVData
    }

}