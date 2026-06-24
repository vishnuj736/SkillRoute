package com.vanakkam.skillroute.controller;

import com.vanakkam.skillroute.model.LearningPath;
import com.vanakkam.skillroute.model.User;
import com.vanakkam.skillroute.repository.UserRepository;
import com.vanakkam.skillroute.service.LearningPathService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/learner/learning-path")
@RequiredArgsConstructor
public class LearningPathController {

    private final LearningPathService learningPathService;
    private final UserRepository userRepository;

    @PostMapping("/generate/{courseId}")
    public ResponseEntity<LearningPath> generatePath(
            @PathVariable Long courseId,
            Authentication authentication) {

        // authentication.getName() returns the email — set by JwtAuthenticationFilter
        // when it validated the token and loaded the UserDetails.
        User learner = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new RuntimeException("Authenticated user not found in database"));

        LearningPath path = learningPathService.generatePath(learner.getId(), courseId);
        return ResponseEntity.ok(path);
    }
}