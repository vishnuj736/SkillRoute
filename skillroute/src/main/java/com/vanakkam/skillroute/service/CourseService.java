package com.vanakkam.skillroute.service;

import com.vanakkam.skillroute.dto.CourseRequest;
import com.vanakkam.skillroute.dto.ModuleRequest;
import com.vanakkam.skillroute.model.Course;
import com.vanakkam.skillroute.model.Module;
import com.vanakkam.skillroute.repository.CourseRepository;
import com.vanakkam.skillroute.repository.ModuleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor // Automatically injects repositories via constructor
public class CourseService {

    private final CourseRepository courseRepository;
    private final ModuleRepository moduleRepository;

    @Transactional
    public Course createCourse(CourseRequest request) {
        Course course = Course.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .build();
        return courseRepository.save(course);
    }

    @Transactional
    public Module addModuleToCourse(Long courseId, ModuleRequest request) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found with id: " + courseId));

        Module module = Module.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .contentUrl(request.getContentUrl())
                .sequenceOrder(request.getSequenceOrder())
                .difficultyTag(request.getDifficultyTag())
                .course(course)
                .build();

        return moduleRepository.save(module);
    }

    @Transactional(readOnly = true)
    public List<Course> getAllCourses() {
        return courseRepository.findAll();
    }
}