package com.vanakkam.skillroute.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "courses")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 255)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    // Direct relationship: A course manages its modules natively.
    // CascadeType.ALL ensures when a course is deleted, its modules go with it.
    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true)

    @JsonManagedReference
    private List<Module> modules;
}