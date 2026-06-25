package com.vanakkam.skillroute.controller;

import com.vanakkam.skillroute.dto.CourseAnalyticsResponse;
import com.vanakkam.skillroute.service.AdminAnalyticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/analytics")
@RequiredArgsConstructor
public class AdminAnalyticsController {

    private final AdminAnalyticsService analyticsService;

    @GetMapping("/course/{courseId}")
    public ResponseEntity<CourseAnalyticsResponse> getCourseAnalytics(
            @PathVariable Long courseId) {
        return ResponseEntity.ok(analyticsService.getCourseAnalytics(courseId));
    }
}