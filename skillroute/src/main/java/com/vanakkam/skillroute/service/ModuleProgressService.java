package com.vanakkam.skillroute.service;

import com.vanakkam.skillroute.dto.ProgressResponse;
import com.vanakkam.skillroute.model.*;
import com.vanakkam.skillroute.model.Module;
import com.vanakkam.skillroute.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ModuleProgressService {

    private final ModuleProgressRepository progressRepository;
    private final ModuleRepository moduleRepository;
    private final CourseRepository courseRepository;
    private final UserRepository userRepository;

    @Transactional
    public ProgressResponse markModuleComplete(Long userId, Long moduleId) {

        Module module = moduleRepository.findById(moduleId)
                .orElseThrow(() -> new RuntimeException("Module not found"));

        Course course = module.getCourse();
        User user = userRepository.findById(userId).orElseThrow();

        // Check if progress record already exists — update it rather than duplicate
        ModuleProgress progress = progressRepository
                .findByUserIdAndModuleId(userId, moduleId)
                .orElse(ModuleProgress.builder()
                        .user(user)
                        .module(module)
                        .course(course)
                        .build());

        progress.setCompleted(true);
        progress.setCompletedAt(LocalDateTime.now());
        progressRepository.save(progress);

        // Calculate progress bar values
        long completedCount = progressRepository
                .countByUserIdAndCourseIdAndCompletedTrue(userId, course.getId());

        List<Module> allModules = moduleRepository
                .findByCourseIdOrderBySequenceOrderAsc(course.getId());

        int totalModules = allModules.size();
        double percentage = totalModules > 0
                ? Math.round((completedCount * 100.0 / totalModules) * 10.0) / 10.0
                : 0.0;

        return ProgressResponse.builder()
                .moduleId(moduleId)
                .moduleTitle(module.getTitle())
                .completed(true)
                .completedAt(progress.getCompletedAt().toString())
                .completedModules((int) completedCount)
                .totalModules(totalModules)
                .completionPercentage(percentage)
                .build();
    }

    // Called by the polling endpoint every 10s from the frontend
    public ProgressResponse getCourseProgress(Long userId, Long courseId) {

        long completedCount = progressRepository
                .countByUserIdAndCourseIdAndCompletedTrue(userId, courseId);

        List<Module> allModules = moduleRepository
                .findByCourseIdOrderBySequenceOrderAsc(courseId);

        int totalModules = allModules.size();
        double percentage = totalModules > 0
                ? Math.round((completedCount * 100.0 / totalModules) * 10.0) / 10.0
                : 0.0;

        return ProgressResponse.builder()
                .completedModules((int) completedCount)
                .totalModules(totalModules)
                .completionPercentage(percentage)
                .build();
    }
}