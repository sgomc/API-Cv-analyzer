package com.example.cv_analyzer_api;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class GeminiRequest {
    private List<Content> contents;
    @JsonProperty("generation_config") // Match the exact JSON field name
    private GenerationConfig generationConfig;
    private List<Tool> tools;
    @JsonProperty("system_instruction")
    private Content systemInstruction;
}