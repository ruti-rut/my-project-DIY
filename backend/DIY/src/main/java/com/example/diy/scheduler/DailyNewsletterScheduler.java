package com.example.diy.scheduler;

import com.example.diy.model.Users;
import com.example.diy.service.AIChatService;
import com.example.diy.service.EmailService;
import com.example.diy.service.NewsletterService;
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
    private NewsletterService newsletterService;

    @Scheduled(cron = "0 38 18 * * ?")
//@Scheduled(cron = "0 * * * * ?")

public void sendDailyEmails() {
        // שולפים רק משתמשים שאישרו מייל וגם נרשמו לניוזלטר
        List<Users> users = usersRepository.findAllByIsSubscribedToDailyTrueAndEmailVerifiedTrue();

        for (Users user : users) {
            try {
                newsletterService.createAndSendNewsletter(user);
//                Thread.sleep(500);
            } catch (Exception e) {
                System.out.println("Error sending email to: " + user.getMail());
                e.printStackTrace();
            }
        }
    }
}
