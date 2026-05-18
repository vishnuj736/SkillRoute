package com.vanakkam.skillroute.repository;

import com.vanakkam.skillroute.model.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {
    // Basic CRUD operations are natively supported out-of-the-box
}