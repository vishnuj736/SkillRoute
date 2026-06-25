package com.vanakkam.skillroute.service;

import com.vanakkam.skillroute.dto.AnswerSubmission;
import com.vanakkam.skillroute.dto.AssessmentSubmissionRequest;
import com.vanakkam.skillroute.exception.ResourceNotFoundException;
import com.vanakkam.skillroute.model.*;
import com.vanakkam.skillroute.repository.AssessmentRepository;
import com.vanakkam.skillroute.repository.AssessmentResultRepository;
import com.vanakkam.skillroute.repository.QuestionRepository;
import com.vanakkam.skillroute.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AssessmentResultService {

    private final AssessmentRepository assessmentRepository;
    private final QuestionRepository questionRepository;
    private final AssessmentResultRepository resultRepository;
    private final UserRepository userRepository;

    @Transactional
    public AssessmentResult submitAssessment(Long assessmentId, AssessmentSubmissionRequest request, Authentication authentication) {

        // 1. Identify the logged-in learner from the JWT-authenticated context
        User learner = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new RuntimeException("Learner not found"));

        Assessment assessment = assessmentRepository.findById(assessmentId)
                .orElseThrow(() -> new ResourceNotFoundException("No assessment found for course ID: " + assessmentId));

        int totalQuestions = request.getAnswers().size();
        int score = 0;
        Set<String> weakTags = new HashSet<>();

        // 2. Loop through each submitted answer, look up the real question, and grade it
        for (AnswerSubmission answer : request.getAnswers()) {
            Question question = questionRepository.findById(answer.getQuestionId())
                    .orElseThrow(() -> new RuntimeException("Question not found: " + answer.getQuestionId()));

            if (answer.isCorrect()) {
                score++;
            } else {
                // Track the skill tag of every question they got wrong
                weakTags.add(question.getSkillTag());
            }
        }

        // 3. Build the comma-separated weak skills string for the AI prompt later
        String weakSkillTags = String.join(", ", weakTags);

        AssessmentResult result = AssessmentResult.builder()
                .user(learner)
                .assessment(assessment)
                .score(score)
                .totalQuestions(totalQuestions)
                .weakSkillTags(weakSkillTags)
                .build();

        return resultRepository.save(result);
    }
}