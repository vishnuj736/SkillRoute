package com.vanakkam.skillroute.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CourseAnalyticsResponse {

    // Metric 1: Overall completion rate
    private int totalLearners;
    private int completedLearners;
    private double completionRate; // percentage

    // Metric 2: Drop-off module
    private Long dropOffModuleId;
    private String dropOffModuleTitle;
    private int dropOffCount;

    // Metric 3: Per-learner health scores
    private List<LearnerHealthScore> learnerHealthScores;

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class LearnerHealthScore {
        private Long userId;
        private String learnerName;
        private String learnerEmail;
        private int completedModules;
        private int totalModules;
        private double completionPercentage;
        private double healthScore; // composite 0-100
        private String healthStatus; // HEALTHY, AT_RISK, INACTIVE
    }
}