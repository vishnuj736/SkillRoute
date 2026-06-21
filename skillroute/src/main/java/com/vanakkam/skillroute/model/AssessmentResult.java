package com.vanakkam.skillroute.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "assessment_results")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AssessmentResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Link to the Learner who took the test
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // Link to the Assessment they took
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assessment_id", nullable = false)
    private Assessment assessment;

    // Their total score (e.g., 8 out of 10)
    @Column(nullable = false)
    private Integer score;

    @Column(name = "total_questions", nullable = false)
    private Integer totalQuestions;

    // This is the "Secret Sauce" for our AI!
    // We will store a comma-separated list of tags they failed (e.g., "Java Basics, React Hooks")
    @Column(name = "weak_skill_tags", columnDefinition = "TEXT")
    private String weakSkillTags;
}