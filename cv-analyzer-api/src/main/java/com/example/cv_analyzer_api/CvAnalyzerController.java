package com.example.cv_analyzer_api;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

// @RestController tells Spring this class will handle web requests and return JSON data.
@RestController
// @RequestMapping provides a base path for all endpoints in this controller (e.g., /api/v1/analyze)
@RequestMapping("/api/v1")
// @CrossOrigin allows requests from other origins (like your frontend running on localhost:3000).
// This is crucial for development.
@CrossOrigin(origins = "https://ai-cv-test-zeta.vercel.app") // Be more specific in production!
public class CvAnalyzerController {

    // We need an instance of our service to do the actual work.
    private final GeminiVisionService geminiVisionService;

    // This is "Constructor Injection" - the best way to get dependencies in Spring.
    // Spring will automatically provide the GeminiVisionService bean.
    public CvAnalyzerController(GeminiVisionService geminiVisionService) {
        this.geminiVisionService = geminiVisionService;
    }

    // @PostMapping defines the specific endpoint path and maps it to HTTP POST requests.
    // The full URL will be http://localhost:8080/api/v1/analyze
    @PostMapping("/analyze")
    // We use ResponseEntity to have full control over the HTTP response (status code, headers, body).
    public ResponseEntity<?> analyzeCv(
            // @RequestParam("file") tells Spring to expect a part in the multipart request named "file".
            @RequestParam("file") MultipartFile imageFile) {

        try {
            // Check for an empty file upload.
            if (imageFile.isEmpty()) {
                return ResponseEntity.badRequest().body("Please upload a file.");
            }

            // Call our service to do the heavy lifting.
            CVData result = geminiVisionService.analyzeCv(imageFile);

            // If everything is successful, return a 200 OK status with the CV data as the JSON body.
            return ResponseEntity.ok(result);

        } catch (IllegalArgumentException e) {
            // This catches specific client-side errors, like providing no file.
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (IOException e) {
            // This catches errors related to reading the file.
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error processing file: " + e.getMessage());
        } catch (RuntimeException e) {
            // This is a catch-all for any other errors from our service (e.g., API call failed).
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An internal error occurred: " + e.getMessage());
        }
    }
}