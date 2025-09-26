package com.example.cv_analyzer_api;

import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class SkillCategory {
    private String categoryTitle;
    private List<String> skills;
}