package com.vanakkam.skillroute.repository;

import com.vanakkam.skillroute.model.AssessmentResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AssessmentResultRepository extends JpaRepository<AssessmentResult, Long> {
    Optional<AssessmentResult> findTopByUserIdAndAssessmentIdOrderByIdDesc(Long userId, Long assessmentId);
}