package com.vanakkam.skillroute.service;

import com.vanakkam.skillroute.model.User;
import com.vanakkam.skillroute.repository.ModuleProgressRepository;
import com.vanakkam.skillroute.repository.ModuleRepository;
import com.vanakkam.skillroute.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class WeeklyEmailScheduler {

    private final UserRepository userRepository;
    private final ModuleProgressRepository progressRepository;
    private final ModuleRepository moduleRepository;
    private final EmailService emailService;

    // Runs every Monday at 9:00 AM
    // Cron format: second minute hour day-of-month month day-of-week
    @Scheduled(cron = "0 0 9 * * MON")
    public void sendWeeklyProgressEmails() {
        log.info("Starting weekly progress email job...");

        // Fetch all learners
        List<User> allUsers = userRepository.findAll();

        int sent = 0;
        int failed = 0;

        for (User user : allUsers) {
            // Only send to learners, not admins
            if (!"ROLE_LEARNER".equals(user.getRole().name())) {
                continue;
            }

            try {
                // For simplicity we use course ID 1 here.
                // In a real multi-course platform you'd loop over each enrolled course.
                Long courseId = 1L;

                long completedCount = progressRepository
                        .countByUserIdAndCourseIdAndCompletedTrue(user.getId(), courseId);

                long totalModules = moduleRepository
                        .findByCourseIdOrderBySequenceOrderAsc(courseId)
                        .size();

                double percentage = totalModules > 0
                        ? Math.round((completedCount * 100.0 / totalModules) * 10.0) / 10.0
                        : 0.0;

                emailService.sendWeeklyProgressEmail(
                        user,
                        (int) completedCount,
                        (int) totalModules,
                        percentage
                );

                sent++;
                log.info("Weekly email sent to {}", user.getEmail());

            } catch (Exception e) {
                failed++;
                log.error("Failed to send weekly email to {}: {}",
                        user.getEmail(), e.getMessage());
            }
        }

        log.info("Weekly email job complete. Sent: {}, Failed: {}", sent, failed);
    }

    // Test trigger — call this endpoint to fire the job immediately
    // without waiting for Monday. Remove before production.
    public void triggerManually() {
        sendWeeklyProgressEmails();
    }
}