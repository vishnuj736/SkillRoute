package com.vanakkam.skillroute.service;

import com.vanakkam.skillroute.dto.CourseAnalyticsResponse;
import com.vanakkam.skillroute.dto.CourseAnalyticsResponse.LearnerHealthScore;
import com.vanakkam.skillroute.model.Module;
import com.vanakkam.skillroute.model.User;
import com.vanakkam.skillroute.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminAnalyticsService {

    private final UserRepository userRepository;
    private final ModuleRepository moduleRepository;
    private final ModuleProgressRepository progressRepository;
    private final LearningPathRepository learningPathRepository;

    public CourseAnalyticsResponse getCourseAnalytics(Long courseId) {

        // All modules in this course
        List<Module> allModules = moduleRepository
                .findByCourseIdOrderBySequenceOrderAsc(courseId);
        int totalModules = allModules.size();

        // All learners
        List<User> learners = userRepository.findAll().stream()
                .filter(u -> "ROLE_LEARNER".equals(u.getRole().name()))
                .collect(Collectors.toList());

        int totalLearners = learners.size();

        // ── Metric 1: Completion Rate ──────────────────────────────────────
        int completedLearners = 0;
        for (User learner : learners) {
            long completed = progressRepository
                    .countByUserIdAndCourseIdAndCompletedTrue(
                            learner.getId(), courseId);
            if (completed >= totalModules && totalModules > 0) {
                completedLearners++;
            }
        }

        double completionRate = totalLearners > 0
                ? Math.round((completedLearners * 100.0 / totalLearners) * 10.0) / 10.0
                : 0.0;

        // ── Metric 2: Drop-Off Module ──────────────────────────────────────
        // The drop-off module is the first module that a significant number
        // of learners have NOT completed — i.e. where progress stops.
        Module dropOffModule = findDropOffModule(allModules, learners, courseId);

        // ── Metric 3: Learner Health Scores ───────────────────────────────
        List<LearnerHealthScore> healthScores = learners.stream()
                .map(learner -> computeHealthScore(learner, courseId, totalModules))
                .sorted(Comparator.comparingDouble(LearnerHealthScore::getHealthScore).reversed())
                .collect(Collectors.toList());

        return CourseAnalyticsResponse.builder()
                .totalLearners(totalLearners)
                .completedLearners(completedLearners)
                .completionRate(completionRate)
                .dropOffModuleId(dropOffModule != null ? dropOffModule.getId() : null)
                .dropOffModuleTitle(dropOffModule != null ? dropOffModule.getTitle() : "None")
                .dropOffCount(dropOffModule != null
                        ? countLearnersWhoStoppedAt(dropOffModule, learners, courseId) : 0)
                .learnerHealthScores(healthScores)
                .build();
    }

    /**
     * Drop-off logic:
     * For each module in sequence order, count how many learners
     * have NOT completed it. The module with the highest "not completed"
     * count that comes after at least one completed module is the drop-off point.
     */
    private Module findDropOffModule(List<Module> modules,
                                     List<User> learners, Long courseId) {
        if (modules.isEmpty() || learners.isEmpty()) return null;

        Module dropOff = null;
        int maxDropOff = 0;

        for (Module module : modules) {
            int notCompletedCount = 0;
            for (User learner : learners) {
                boolean completed = progressRepository
                        .findByUserIdAndModuleId(learner.getId(), module.getId())
                        .map(p -> p.getCompleted())
                        .orElse(false);
                if (!completed) {
                    notCompletedCount++;
                }
            }

            if (notCompletedCount > maxDropOff) {
                maxDropOff = notCompletedCount;
                dropOff = module;
            }
        }

        return dropOff;
    }

    private int countLearnersWhoStoppedAt(Module module,
                                          List<User> learners, Long courseId) {
        int count = 0;
        for (User learner : learners) {
            boolean completed = progressRepository
                    .findByUserIdAndModuleId(learner.getId(), module.getId())
                    .map(p -> p.getCompleted())
                    .orElse(false);
            if (!completed) count++;
        }
        return count;
    }

    /**
     * Health Score Formula (from the spec):
     * (completion % × 0.5) + (login recency score × 0.3) + (avg module score × 0.2)
     *
     * Since we don't track login recency or module scores yet,
     * we use completion % as the primary signal and apply a
     * simplified formula: healthScore = completion % weighted by pace.
     *
     * This is flagged as a Phase 4 enhancement to add recency tracking.
     */
    private LearnerHealthScore computeHealthScore(User learner,
                                                  Long courseId, int totalModules) {
        long completedCount = progressRepository
                .countByUserIdAndCourseIdAndCompletedTrue(learner.getId(), courseId);

        double completionPct = totalModules > 0
                ? Math.round((completedCount * 100.0 / totalModules) * 10.0) / 10.0
                : 0.0;

        // Simplified health score — completion % weighted at 0.5,
        // path generation (shows engagement) weighted at 0.5
        boolean hasGeneratedPath = learningPathRepository
                .findTopByUserIdAndCourseIdOrderByIdDesc(learner.getId(), courseId)
                .isPresent();

        double engagementScore = hasGeneratedPath ? 100.0 : 0.0;
        double healthScore = Math.round(
                ((completionPct * 0.5) + (engagementScore * 0.5)) * 10.0) / 10.0;

        String healthStatus;
        if (healthScore >= 70) {
            healthStatus = "HEALTHY";
        } else if (healthScore >= 30) {
            healthStatus = "AT_RISK";
        } else {
            healthStatus = "INACTIVE";
        }

        return LearnerHealthScore.builder()
                .userId(learner.getId())
                .learnerName(learner.getName())
                .learnerEmail(learner.getEmail())
                .completedModules((int) completedCount)
                .totalModules(totalModules)
                .completionPercentage(completionPct)
                .healthScore(healthScore)
                .healthStatus(healthStatus)
                .build();
    }
}