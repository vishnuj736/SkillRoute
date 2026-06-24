package com.vanakkam.skillroute.repository;

import com.vanakkam.skillroute.model.LearningPath;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LearningPathRepository extends JpaRepository<LearningPath, Long> {
    Optional<LearningPath> findTopByUserIdAndCourseIdOrderByIdDesc(Long userId, Long courseId);
}