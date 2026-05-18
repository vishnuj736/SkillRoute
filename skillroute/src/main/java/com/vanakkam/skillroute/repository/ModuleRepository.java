package com.vanakkam.skillroute.repository;

import com.vanakkam.skillroute.model.Module;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ModuleRepository extends JpaRepository<Module, Long> {
    // Custom query method to find modules belonging to a specific course sorted by sequence
    List<Module> findByCourseIdOrderBySequenceOrderAsc(Long courseId);
}