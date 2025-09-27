package com.example.cv_analyzer_api;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Tool {
    @JsonProperty("function_declarations")
    private List<FunctionDeclaration> functionDeclarations;
}