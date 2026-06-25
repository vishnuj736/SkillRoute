package com.vanakkam.skillroute.controller;

import com.vanakkam.skillroute.dto.CourseAnalyticsResponse;
import com.vanakkam.skillroute.service.AdminAnalyticsService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@RestController
@RequestMapping("/api/admin/analytics")
@RequiredArgsConstructor
public class AdminAnalyticsController {

    private final AdminAnalyticsService analyticsService;
    private final Map<String, AtomicInteger> requestCountMap
            = new ConcurrentHashMap<>();

    @GetMapping("/course/{courseId}")
    public ResponseEntity<?> getCourseAnalytics(
            @PathVariable Long courseId,
            HttpServletRequest request) {

        String ip = request.getRemoteAddr();
        requestCountMap.putIfAbsent(ip, new AtomicInteger(0));
        int count = requestCountMap.get(ip).incrementAndGet();

        if (count > 10) {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                    .body(Map.of("message",
                            "Rate limit exceeded. Try again later."));
        }

        return ResponseEntity.ok(
                analyticsService.getCourseAnalytics(courseId));
    }
}