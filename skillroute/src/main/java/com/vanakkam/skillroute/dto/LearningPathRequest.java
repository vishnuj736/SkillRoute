package com.vanakkam.skillroute.dto;

import lombok.Data;
import java.util.List;

@Data
public class LearningPathRequest {
    private String weakSkillTags;       // e.g. "Java Basics, React Hooks"
    private List<ModuleSummary> modules; // the course's available modules
}