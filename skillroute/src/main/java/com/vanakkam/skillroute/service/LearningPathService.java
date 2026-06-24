package com.vanakkam.skillroute.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vanakkam.skillroute.dto.ModuleSummary;
import com.vanakkam.skillroute.model.*;
import com.vanakkam.skillroute.model.Module;
import com.vanakkam.skillroute.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LearningPathService {

    private final ModuleRepository moduleRepository;
    private final UserRepository userRepository;
    private final CourseRepository courseRepository;
    private final AssessmentRepository assessmentRepository;
    private final AssessmentResultRepository assessmentResultRepository;
    private final LearningPathRepository learningPathRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Transactional
    public LearningPath generatePath(Long userId, Long courseId) {

        Assessment assessment = assessmentRepository.findByCourseId(courseId)
                .orElseThrow(() -> new RuntimeException("No assessment found for this course"));

        AssessmentResult result = assessmentResultRepository
                .findTopByUserIdAndAssessmentIdOrderByIdDesc(userId, assessment.getId())
                .orElseThrow(() -> new RuntimeException("No assessment result found. Please submit the assessment first."));

        List<Module> modules = moduleRepository.findByCourseIdOrderBySequenceOrderAsc(courseId);

        if (modules.isEmpty()) {
            throw new RuntimeException("No modules found for this course");
        }

        Set<String> weakTags = parseWeakTags(result.getWeakSkillTags());

        // Score each module
        Map<Module, Integer> scoreMap = new HashMap<>();
        for (Module module : modules) {
            int score = 0;
            String titleLower = module.getTitle().toLowerCase();

            for (String tag : weakTags) {
                String tagLower = tag.toLowerCase().trim();
                if (titleLower.contains(tagLower)) {
                    score += 3;
                }
                for (String word : tagLower.split("\\s+")) {
                    if (word.length() > 2 && titleLower.contains(word)) {
                        score += 1;
                    }
                }
            }

            if (!weakTags.isEmpty() && "Beginner".equalsIgnoreCase(module.getDifficultyTag())) {
                score += 2;
            }
            if ("Advanced".equalsIgnoreCase(module.getDifficultyTag()) && score == 0) {
                score -= 1;
            }

            scoreMap.put(module, score);
        }

        // Sort highest score first, original order as tiebreaker
        List<Module> sorted = modules.stream()
                .sorted(Comparator
                        .comparingInt((Module m) -> scoreMap.get(m))
                        .reversed()
                        .thenComparingInt(Module::getSequenceOrder))
                .collect(Collectors.toList());

        // Build path JSON
        try {
            List<Map<String, Object>> pathList = new ArrayList<>();
            int order = 1;
            for (Module m : sorted) {
                int score = scoreMap.get(m);
                Map<String, Object> entry = new LinkedHashMap<>();
                entry.put("moduleId", m.getId());
                entry.put("order", order++);
                entry.put("title", m.getTitle());
                entry.put("difficultyTag", m.getDifficultyTag());
                entry.put("reason", buildReason(m, weakTags, score));
                pathList.add(entry);
            }

            Map<String, Object> pathResult = new LinkedHashMap<>();
            pathResult.put("path", pathList);
            pathResult.put("generatedAt", LocalDateTime.now().toString());
            pathResult.put("algorithm", "skill-tag-scoring-v1");

            String pathJson = objectMapper.writeValueAsString(pathResult);

            User user = userRepository.findById(userId).orElseThrow();
            Course course = courseRepository.findById(courseId).orElseThrow();

            LearningPath path = LearningPath.builder()
                    .user(user)
                    .course(course)
                    .pathJson(pathJson)
                    .createdAt(LocalDateTime.now())
                    .build();

            return learningPathRepository.save(path);

        } catch (Exception e) {
            throw new RuntimeException("Failed to generate learning path", e);
        }
    }

    /**
     * Core algorithm: scores each module based on how closely it matches
     * the learner's weak skill tags, then reorders accordingly.
     *
     * Scoring rules:
     *  +3 points  — module title directly contains a weak skill tag (highest priority)
     *  +2 points  — module difficultyTag is "Beginner" and learner has weak tags (foundational first)
     *  +1 point   — module title partially matches any weak tag word
     *  -1 point   — module difficultyTag is "Advanced" (push to end if not a weak area)
     */
    private List<ModuleSummary> buildPersonalizedPath(List<Module> modules, Set<String> weakTags) {

        // Score each module
        Map<Module, Integer> scoreMap = new HashMap<>();

        for (Module module : modules) {
            int score = 0;
            String titleLower = module.getTitle().toLowerCase();

            for (String tag : weakTags) {
                String tagLower = tag.toLowerCase().trim();

                // Direct title match — highest signal
                if (titleLower.contains(tagLower)) {
                    score += 3;
                }

                // Partial word match — e.g. "Java" matches "Java Basics" tag
                String[] tagWords = tagLower.split("\\s+");
                for (String word : tagWords) {
                    if (word.length() > 2 && titleLower.contains(word)) {
                        score += 1;
                    }
                }
            }

            // Foundational modules get a boost if the learner has weak areas
            if (!weakTags.isEmpty() && "Beginner".equalsIgnoreCase(module.getDifficultyTag())) {
                score += 2;
            }

            // Advanced modules get deprioritized unless they directly match a weak tag
            if ("Advanced".equalsIgnoreCase(module.getDifficultyTag()) && score == 0) {
                score -= 1;
            }

            scoreMap.put(module, score);
        }

        // Sort: highest score first, preserve original sequenceOrder as tiebreaker
        List<Module> sorted = modules.stream()
                .sorted(Comparator
                        .comparingInt((Module m) -> scoreMap.get(m))
                        .reversed()
                        .thenComparingInt(Module::getSequenceOrder))
                .collect(Collectors.toList());

        // Build the ordered path with reasons
        List<ModuleSummary> result = new ArrayList<>();
        int order = 1;
        for (Module m : sorted) {
            int score = scoreMap.get(m);
            String reason = buildReason(m, weakTags, score);

            ModuleSummary summary = new ModuleSummary();
            summary.setId(m.getId());
            summary.setTitle(m.getTitle());
            summary.setDifficultyTag(m.getDifficultyTag());
            summary.setSequenceOrder(order++);

            result.add(summary);
        }

        return result;
    }

    private String buildReason(Module module, Set<String> weakTags, int score) {
        if (score >= 3) {
            return "Prioritized: directly addresses your weak area in " + module.getTitle();
        } else if (score == 2) {
            return "Prioritized: foundational module recommended before advancing";
        } else if (score == 1) {
            return "Recommended: partially related to your weak skill areas";
        } else if (score < 0) {
            return "Scheduled last: advanced topic, complete earlier modules first";
        } else {
            return "Scheduled in default order: no specific weak area overlap";
        }
    }

    private Set<String> parseWeakTags(String weakSkillTags) {
        if (weakSkillTags == null || weakSkillTags.isBlank()) {
            return Collections.emptySet();
        }
        return Arrays.stream(weakSkillTags.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toSet());
    }

    private String serializePath(List<ModuleSummary> path) {
        try {
            Map<String, Object> result = new HashMap<>();
            List<Map<String, Object>> pathList = new ArrayList<>();

            for (ModuleSummary m : path) {
                Map<String, Object> entry = new LinkedHashMap<>();
                entry.put("moduleId", m.getId());
                entry.put("order", m.getSequenceOrder());
                entry.put("title", m.getTitle());
                entry.put("difficultyTag", m.getDifficultyTag());
                entry.put("reason", buildReason(null, Collections.emptySet(), 0));
                pathList.add(entry);
            }

            result.put("path", pathList);
            result.put("generatedAt", LocalDateTime.now().toString());
            result.put("algorithm", "skill-tag-scoring-v1");

            return objectMapper.writeValueAsString(result);
        } catch (Exception e) {
            throw new RuntimeException("Failed to serialize learning path", e);
        }
    }
}