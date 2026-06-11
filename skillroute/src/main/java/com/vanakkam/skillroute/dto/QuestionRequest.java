package com.vanakkam.skillroute.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class QuestionRequest {
    @NotBlank(message = "Question text is required")
    private String questionText;

    @NotBlank(message = "Skill tag is required for AI processing")
    private String skillTag;
}