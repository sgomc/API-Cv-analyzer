package com.example.cv_analyzer_api;
// Helper classes for the request payload (can be nested or separate files)

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
class Content {

	private List<Part> parts;
}

@Data
@AllArgsConstructor
class Part {
    private InlineData inlineData;
    private String text; // Also used for the system prompt

    // Constructor for image part
    public Part(InlineData inlineData) {
        this.inlineData = inlineData;
        this.text = null;
    }

    // Constructor for text part
    public Part(String text) {
        this.text = text;
        this.inlineData = null;
    }
}

@Data
@AllArgsConstructor
class InlineData {
    @JsonProperty("mime_type")
    private String mimeType;
    private String data;
}

@Data
@AllArgsConstructor
class GenerationConfig {
    @JsonProperty("response_mime_type")
    private String responseMimeType;
}

@Data
@AllArgsConstructor
@NoArgsConstructor
class FunctionDeclaration {
    private String name;
    private String description;
    private Object parameters; // Using Object to hold the raw schema map
}