package com.vanakkam.skillroute.controller;

import com.vanakkam.skillroute.dto.AssessmentSubmissionRequest;
import com.vanakkam.skillroute.model.AssessmentResult;
import com.vanakkam.skillroute.service.AssessmentResultService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/learner/assessments")
@RequiredArgsConstructor
public class LearnerAssessmentController {

    private final AssessmentResultService assessmentResultService;

    @PostMapping("/{assessmentId}/submit")
    public ResponseEntity<AssessmentResult> submit(
            @PathVariable Long assessmentId,
            @RequestBody AssessmentSubmissionRequest request,
            Authentication authentication) {

        AssessmentResult result = assessmentResultService.submitAssessment(assessmentId, request, authentication);
        return new ResponseEntity<>(result, HttpStatus.CREATED);
    }
}