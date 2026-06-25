package com.vanakkam.skillroute.service;

import com.vanakkam.skillroute.model.EmailLog;
import com.vanakkam.skillroute.model.LearningPath;
import com.vanakkam.skillroute.model.User;
import com.vanakkam.skillroute.repository.EmailLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.internet.MimeMessage;
import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;
    private final EmailLogRepository emailLogRepository;

    @Value("${app.mail.from}")
    private String fromAddress;

    public void sendWelcomeEmail(User learner, LearningPath path) {

        // Don't send duplicate welcome emails
        if (emailLogRepository.existsByUserIdAndEmailType(
                learner.getId(), "WELCOME")) {
            log.info("Welcome email already sent to user {}", learner.getId());
            return;
        }

        String subject = "Welcome to SkillRoute, " + learner.getName() + "!";
        String body = buildWelcomeEmailBody(learner, path);

        sendEmail(learner, subject, body, "WELCOME");
    }

    private void sendEmail(User user, String subject, String body, String emailType) {
        EmailLog log_entry = EmailLog.builder()
                .user(user)
                .emailType(emailType)
                .toAddress(user.getEmail())
                .subject(subject)
                .status("PENDING")
                .build();

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromAddress);
            helper.setTo(user.getEmail());
            helper.setSubject(subject);
            helper.setText(body, true); // true = HTML content

            mailSender.send(message);

            log_entry.setStatus("SENT");
            log_entry.setSentAt(LocalDateTime.now());
            log.info("Email sent successfully to {}", user.getEmail());

        } catch (Exception e) {
            log_entry.setStatus("FAILED");
            log_entry.setErrorMessage(e.getMessage());
            log.error("Failed to send email to {}: {}", user.getEmail(), e.getMessage());
        } finally {
            emailLogRepository.save(log_entry);
        }
    }

    private String buildWelcomeEmailBody(User learner, LearningPath path) {
        return """
                <html>
                <body style="font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto;">
                    <div style="background-color: #4F46E5; padding: 30px; text-align: center;">
                        <h1 style="color: white; margin: 0;">Welcome to SkillRoute!</h1>
                    </div>
                    <div style="padding: 30px; background-color: #f9f9f9;">
                        <h2>Vanakkam, %s! 👋</h2>
                        <p>Your personalized learning path has been generated based on your assessment results.</p>
                        <p>We've identified your weak areas and created a custom module order just for you.</p>
                        <div style="background-color: #EEF2FF; border-left: 4px solid #4F46E5; padding: 15px; margin: 20px 0;">
                            <p style="margin: 0;"><strong>Your path was generated on:</strong> %s</p>
                            <p style="margin: 5px 0 0 0;"><strong>Algorithm:</strong> Skill-Tag Scoring v1</p>
                        </div>
                        <p>Log in to your dashboard to see your personalized module order and start learning!</p>
                        <div style="text-align: center; margin: 30px 0;">
                            <a href="http://localhost:3000/dashboard"
                               style="background-color: #4F46E5; color: white; padding: 12px 30px;
                                      text-decoration: none; border-radius: 5px; font-weight: bold;">
                                Go to My Dashboard
                            </a>
                        </div>
                        <p style="color: #666; font-size: 12px;">
                            You're receiving this because you joined SkillRoute.
                        </p>
                    </div>
                </body>
                </html>
                """.formatted(learner.getName(), path.getCreatedAt().toString());
    }
    public void sendWeeklyProgressEmail(User learner, int completedModules,
                                        int totalModules, double percentage) {
        String subject = "Your Weekly Progress on SkillRoute 📊";
        String body = buildWeeklySummaryBody(learner, completedModules,
                totalModules, percentage);
        sendEmail(learner, subject, body, "WEEKLY_SUMMARY");
    }

    private String buildWeeklySummaryBody(User learner, int completedModules,
                                          int totalModules, double percentage) {
        String progressBar = buildProgressBar(percentage);
        String encouragement = getEncouragement(percentage);

        return """
            <html>
            <body style="font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto;">
                <div style="background-color: #4F46E5; padding: 30px; text-align: center;">
                    <h1 style="color: white; margin: 0;">Your Weekly Progress 📊</h1>
                </div>
                <div style="padding: 30px; background-color: #f9f9f9;">
                    <h2>Hey %s! Here's your weekly summary.</h2>
                    <div style="background-color: white; border-radius: 8px;
                                padding: 20px; margin: 20px 0; box-shadow: 0 2px 4px rgba(0,0,0,0.1);">
                        <h3 style="color: #4F46E5; margin-top: 0;">Overall Progress</h3>
                        <p><strong>%d</strong> of <strong>%d</strong> modules completed</p>
                        <div style="background-color: #e5e7eb; border-radius: 9999px;
                                    height: 12px; margin: 10px 0;">
                            <div style="background-color: #4F46E5; height: 12px;
                                        border-radius: 9999px; width: %s;"></div>
                        </div>
                        <p style="color: #4F46E5; font-weight: bold; font-size: 18px;">%s completed</p>
                    </div>
                    <div style="background-color: #EEF2FF; border-radius: 8px;
                                padding: 20px; margin: 20px 0;">
                        <p style="margin: 0; font-size: 16px;">%s</p>
                    </div>
                    <div style="text-align: center; margin: 30px 0;">
                        <a href="http://localhost:3000/dashboard"
                           style="background-color: #4F46E5; color: white; padding: 12px 30px;
                                  text-decoration: none; border-radius: 5px; font-weight: bold;">
                            Continue Learning
                        </a>
                    </div>
                </div>
            </body>
            </html>
            """.formatted(
                learner.getName(),
                completedModules,
                totalModules,
                percentage + "%%",
                percentage + "%%",
                encouragement
        );
    }

    private String buildProgressBar(double percentage) {
        return percentage + "%";
    }

    private String getEncouragement(double percentage) {
        if (percentage == 100) {
            return "🎉 Outstanding! You've completed the entire course. You're ready to build!";
        } else if (percentage >= 75) {
            return "🚀 Almost there! You're in the home stretch — keep pushing!";
        } else if (percentage >= 50) {
            return "💪 Great momentum! You're halfway through your personalized path.";
        } else if (percentage >= 25) {
            return "⭐ Good start! Consistency is the key — try to complete one more module this week.";
        } else {
            return "👋 Your learning journey is just beginning! Log in and complete your first module today.";
        }
    }
}