package com.vanakkam.skillroute.controller;

import com.vanakkam.skillroute.dto.CourseRequest;
import com.vanakkam.skillroute.dto.ModuleRequest;
import com.vanakkam.skillroute.model.Course;
import com.vanakkam.skillroute.model.Module;
import com.vanakkam.skillroute.service.CourseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/courses")
@RequiredArgsConstructor
public class AdminCourseController {

    private final CourseService courseService;

    // POST: Create a new Course
    @PostMapping
    public ResponseEntity<Course> createCourse(@Valid @RequestBody CourseRequest request) {
        Course savedCourse = courseService.createCourse(request);
        return new ResponseEntity<>(savedCourse, HttpStatus.CREATED);
    }

    // POST: Add a Module to an existing Course
    @PostMapping("/{courseId}/modules")
    public ResponseEntity<Module> addModule(@PathVariable Long courseId, @Valid @RequestBody ModuleRequest request) {
        Module savedModule = courseService.addModuleToCourse(courseId, request);
        return new ResponseEntity<>(savedModule, HttpStatus.CREATED);
    }

    // GET: Fetch all courses
    @GetMapping
    public ResponseEntity<List<Course>> getAllCourses() {
        return ResponseEntity.ok(courseService.getAllCourses());
    }
}