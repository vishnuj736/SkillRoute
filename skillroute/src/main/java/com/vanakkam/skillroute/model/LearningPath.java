package com.vanakkam.skillroute.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "learning_paths")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LearningPath {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonBackReference
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    @JsonBackReference
    private Course course;

    // Stores the AI's full structured output as raw JSON text.
    // We store it this way because the "shape" of the path (order + reasons)
    // is naturally a JSON document, not a relational table.
    @Column(name = "path_json", columnDefinition = "TEXT", nullable = false)
    private String pathJson;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
}