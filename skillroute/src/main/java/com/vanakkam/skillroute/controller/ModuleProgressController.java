package com.vanakkam.skillroute.controller;

import com.vanakkam.skillroute.dto.ProgressResponse;
import com.vanakkam.skillroute.model.User;
import com.vanakkam.skillroute.repository.UserRepository;
import com.vanakkam.skillroute.service.ModuleProgressService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/learner/progress")
@RequiredArgsConstructor
public class ModuleProgressController {

    private final ModuleProgressService progressService;
    private final UserRepository userRepository;

    // Mark a module as complete
    @PostMapping("/complete/{moduleId}")
    public ResponseEntity<ProgressResponse> markComplete(
            @PathVariable Long moduleId,
            Authentication authentication) {

        User learner = userRepository.findByEmail(authentication.getName())
                .orElseThrow();

        ProgressResponse response = progressService.markModuleComplete(
                learner.getId(), moduleId);

        return ResponseEntity.ok(response);
    }

    // Polling endpoint — frontend calls this every 10s to update the progress bar
    @GetMapping("/course/{courseId}")
    public ResponseEntity<ProgressResponse> getCourseProgress(
            @PathVariable Long courseId,
            Authentication authentication) {

        User learner = userRepository.findByEmail(authentication.getName())
                .orElseThrow();

        ProgressResponse response = progressService.getCourseProgress(
                learner.getId(), courseId);

        return ResponseEntity.ok(response);
    }
}