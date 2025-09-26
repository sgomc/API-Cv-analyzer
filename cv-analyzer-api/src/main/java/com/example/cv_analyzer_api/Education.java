package com.example.cv_analyzer_api;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Education {
    private String degree;
    private String institution;
    private String location;
    private String graduationDate;
    private String gpa;
}