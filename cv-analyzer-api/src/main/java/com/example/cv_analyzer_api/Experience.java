package com.example.cv_analyzer_api;

import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Experience {
    private String jobTitle;
    private String company;
    private String location;
    private String startDate;
    private String endDate;
    private List<String> responsibilities;
}