package com.example.cv_analyzer_api;

import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Project {
    private String projectTitle;
    private String technologies;
    private String link;
    private List<String> description;
}