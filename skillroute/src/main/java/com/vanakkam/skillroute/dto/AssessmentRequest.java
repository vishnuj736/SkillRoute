package com.vanakkam.skillroute.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AssessmentRequest {
    @NotBlank(message = "Title is required")
    private String title;
}