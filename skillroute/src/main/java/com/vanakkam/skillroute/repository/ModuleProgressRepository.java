package com.vanakkam.skillroute.repository;

import com.vanakkam.skillroute.model.ModuleProgress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ModuleProgressRepository extends JpaRepository<ModuleProgress, Long> {

    // Find a specific learner's progress on a specific module
    Optional<ModuleProgress> findByUserIdAndModuleId(Long userId, Long moduleId);

    // Find all completed modules for a learner in a course
    List<ModuleProgress> findByUserIdAndCourseIdAndCompletedTrue(Long userId, Long courseId);

    // Count completed modules — used for the progress bar calculation
    long countByUserIdAndCourseIdAndCompletedTrue(Long userId, Long courseId);
}