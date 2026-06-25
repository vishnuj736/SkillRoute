package com.vanakkam.skillroute.controller;

import com.vanakkam.skillroute.service.WeeklyEmailScheduler;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dev")
@RequiredArgsConstructor
public class DevToolsController {

    private final WeeklyEmailScheduler weeklyEmailScheduler;

    @PostMapping("/trigger-weekly-email")
    public ResponseEntity<String> triggerWeeklyEmail() {
        weeklyEmailScheduler.triggerManually();
        return ResponseEntity.ok("Weekly email job triggered successfully");
    }
}