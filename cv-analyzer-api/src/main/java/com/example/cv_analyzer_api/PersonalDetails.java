package com.example.cv_analyzer_api;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data // Generates getters, setters, toString(), equals(), and hashCode()
@NoArgsConstructor // Generates a no-argument constructor
public class PersonalDetails {
    private String fullName;
    private String email;
    private String phoneNumber;
    private String address;
    private String linkedin;
    private String portfolio;
}