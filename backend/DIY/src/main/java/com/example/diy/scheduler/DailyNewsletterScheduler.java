package com.example.diy.scheduler;

import com.example.diy.model.Users;
import com.example.diy.service.AIChatService;
import com.example.diy.service.EmailService;
import com.example.diy.service.UsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DailyNewsletterScheduler {
    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private AIChatService aiChatService;

    @Scheduled(cron = "0 0 9 * * ?") // כל יום ב-9:00 בבוקר
    public void sendDailyEmails() {
        List<Users> users = usersRepository.findAllByIsSubscribedToDailyTrueAndEmailVerifiedTrue();
        for (Users user : users) {
            String content = generateEmailContent(user); // נשתמש ב-AI
            emailService.sendEmail(user.getMail(), "השראה חדשה מהאתר", content);
        }
    }

    private String generateEmailContent(Users user) {
        // כאן נשלח Prompt ל-AI לקבלת תוכן מותאם
        String prompt = "צור כותרת קצרה והשראה לפרויקטים DIY מותאם לעונה הנוכחית.";
        // כאן תקבלי Flux<String> מה-AI, אפשר לאחד ל-String אחד
        return aiChatService.getResponse(prompt, "daily-newsletter-" + user.getId()).blockLast();
    }

}
