package com.vanakkam.skillroute.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProgressResponse {
    private Long moduleId;
    private String moduleTitle;
    private Boolean completed;
    private String completedAt;
    private int completedModules;
    private int totalModules;
    private double completionPercentage;  // the progress bar value
}