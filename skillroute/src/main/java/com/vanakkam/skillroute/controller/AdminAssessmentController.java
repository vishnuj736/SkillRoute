package com.vanakkam.skillroute.controller;

import com.vanakkam.skillroute.dto.AssessmentRequest;
import com.vanakkam.skillroute.dto.QuestionRequest;
import com.vanakkam.skillroute.model.Assessment;
import com.vanakkam.skillroute.model.Question;
import com.vanakkam.skillroute.service.AssessmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/assessments")
@RequiredArgsConstructor
public class AdminAssessmentController {

    private final AssessmentService assessmentService;

    @PostMapping("/course/{courseId}")
    public ResponseEntity<Assessment> createAssessment(
            @PathVariable Long courseId,
            @Valid @RequestBody AssessmentRequest request) {
        return new ResponseEntity<>(assessmentService.createAssessment(courseId, request), HttpStatus.CREATED);
    }

    @PostMapping("/{assessmentId}/questions")
    public ResponseEntity<Question> addQuestion(
            @PathVariable Long assessmentId,
            @Valid @RequestBody QuestionRequest request) {
        return new ResponseEntity<>(assessmentService.addQuestion(assessmentId, request), HttpStatus.CREATED);
    }
}