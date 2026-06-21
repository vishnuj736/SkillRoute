package com.vanakkam.skillroute.dto;

import lombok.Data;

@Data
public class AnswerSubmission {
    private Long questionId;
    private boolean correct; // true if the learner answered this correctly
}