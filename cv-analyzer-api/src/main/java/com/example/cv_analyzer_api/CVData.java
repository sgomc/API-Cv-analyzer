package com.example.cv_analyzer_api;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
// This annotation tells the JSON serializer to not include fields that are null.
// This perfectly mimics the "omit if not present" part of your prompt.
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CVData {
    private PersonalDetails personalDetails;
    private String summary;
    private List<Experience> experience;
    private List<Education> education;
    private List<Project> projects;
    private List<SkillCategory> skills;
    private List<CustomSection> customSections;
}