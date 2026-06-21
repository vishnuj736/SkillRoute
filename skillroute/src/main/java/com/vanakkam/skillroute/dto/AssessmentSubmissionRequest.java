package com.vanakkam.skillroute.dto;

import lombok.Data;
import java.util.List;

@Data
public class AssessmentSubmissionRequest {
    private List<AnswerSubmission> answers;
}