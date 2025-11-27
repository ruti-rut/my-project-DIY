package com.example.diy.service;

import com.example.diy.model.Challenge;
import com.example.diy.model.Project;
import com.example.diy.model.Users;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class NewsletterService {
    private static final String BASE_URL = "http://localhost:8080";
    private static final String BASE_URL_FRONTEND = "http://localhost:4200";
    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private AIChatService aiChatService;

    @Autowired
    private EmailSenderService emailSenderService;
    @Autowired
    private ChallengeRepository challengeRepository;

    public void createAndSendNewsletter(Users user) {
        System.out.println("1️⃣ מתחיל הכנת מייל עבור: " + user.getUserName());

        // 1. שליפת פרויקטים ואתגרים
        List<Project> projects = projectRepository.findTop3ByOrderByCreatedAtDesc();
        List<Challenge> challenges = challengeRepository.findTop3ActiveChallenges();

        System.out.println("2️⃣ נמצאו " + projects.size() + " פרויקטים ו-" + challenges.size() + " אתגרים");

        if (projects.isEmpty() && challenges.isEmpty()) {
            System.out.println("⚠️ אין תוכן לשליחה");
            return;
        }

        try {
            // 2. בקשה מורחבת ל-AI - כולל מבנה HTML
            String aiGeneratedContent = aiChatService.generateEnhancedNewsletterContent(
                    user.getUserName(),
                    projects,
                    challenges
            );

            System.out.println("3️⃣ AI יצר תוכן מלא");

            // 3. הרכבת HTML מעוצב
            String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            String subject = "✨ הניוזלטר היומי שלך | רעיונות חדשים ליצירה - " + now;
            String htmlBody = buildEnhancedHtml(user, aiGeneratedContent, projects, challenges);

            // 4. שליחה
            System.out.println("4️⃣ שולח מייל ל-" + user.getMail());
            emailSenderService.send(user.getMail(), subject, htmlBody);
            System.out.println("✅ הצלחה!");

        } catch (Exception e) {
            System.err.println("❌ שגיאה: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private String buildEnhancedHtml(Users user, String aiContent, List<Project> projects, List<Challenge> challenges) {
        StringBuilder html = new StringBuilder();

        html.append("""
            <!DOCTYPE html>
            <html dir="rtl" lang="he">
            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <title>הניוזלטר היומי שלך</title>
            </head>
            <body style="margin: 0; padding: 0; font-family: 'Segoe UI', Tahoma, Arial, sans-serif; background-color: #f5f5f5;">
                <table width="100%" cellpadding="0" cellspacing="0" style="background-color: #f5f5f5;">
                    <tr>
                        <td align="center" style="padding: 20px 0;">
                            <table width="600" cellpadding="0" cellspacing="0" style="background-color: #ffffff; border-radius: 10px; overflow: hidden; box-shadow: 0 2px 10px rgba(0,0,0,0.1);">
                                
                                <!-- Header -->
                                <tr>
                                    <td style="background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); padding: 40px 30px; text-align: center;">
                                        <h1 style="color: #ffffff; margin: 0; font-size: 28px; font-weight: bold;">🎨 DIY יומי</h1>
                                        <p style="color: #ffffff; margin: 10px 0 0 0; font-size: 14px; opacity: 0.9;">הרעיונות הכי טובים שלך להיום</p>
                                    </td>
                                </tr>
                                
                                <!-- AI Generated Content -->
                                <tr>
                                    <td style="padding: 30px;">
            """);

        html.append(aiContent); // התוכן שה-AI יצר

        html.append("""
                                    </td>
                                </tr>
            """);

        // פרויקטים מומלצים
        if (!projects.isEmpty()) {
            html.append("""
                <tr>
                    <td style="padding: 0 30px 20px 30px;">
                        <h2 style="color: #333; font-size: 22px; margin-bottom: 20px; border-bottom: 3px solid #667eea; padding-bottom: 10px;">
                            🔥 פרויקטים חמים השבוע
                        </h2>
                """);

            for (Project p : projects) {
                html.append(buildProjectCard(p));
            }

            html.append("</td></tr>");
        }

        // אתגרים פעילים
        if (!challenges.isEmpty()) {
            html.append("""
                <tr>
                    <td style="padding: 20px 30px;">
                        <h2 style="color: #333; font-size: 22px; margin-bottom: 20px; border-bottom: 3px solid #f093fb; padding-bottom: 10px;">
                            🏆 אתגרים פעילים - הצטרפי עכשיו!
                        </h2>
                """);

            for (Challenge c : challenges) {
                html.append(buildChallengeCard(c));
            }

            html.append("</td></tr>");
        }

        // CTA
        html.append("""
                <tr>
                    <td style="padding: 30px; text-align: center; background-color: #f9f9f9;">
                        <a href="%s" style="display: inline-block; padding: 15px 40px; background: linear-gradient(135deg, #667eea 0%%, #764ba2 100%%); color: #ffffff; text-decoration: none; border-radius: 25px; font-weight: bold; font-size: 16px;">
                            🚀 גלי עוד פרויקטים באתר
                        </a>
                    </td>
                </tr>
                """.formatted(BASE_URL_FRONTEND));

        // Footer
        html.append("""
                <tr>
                    <td style="padding: 20px 30px; background-color: #333; color: #ffffff; text-align: center; font-size: 12px;">
                        <p style="margin: 0 0 10px 0;">💌 קיבלת את המייל הזה כי נרשמת לניוזלטר היומי</p>
                        <p style="margin: 0;">
                            <a href="%s/unsubscribe?email=%s" style="color: #667eea; text-decoration: none;">ביטול הרשמה</a> | 
                            <a href="%s" style="color: #667eea; text-decoration: none;">האתר שלנו</a>
                        </p>
                        <p style="margin: 10px 0 0 0; opacity: 0.7;">© 2025 DIY Community. All rights reserved.</p>
                    </td>
                </tr>
                
            </table>
        </td>
    </tr>
</table>
</body>
</html>
            """.formatted(BASE_URL_FRONTEND, user.getMail(), BASE_URL_FRONTEND));

        return html.toString();
    }

    private String buildProjectCard(Project p) {
        String link = BASE_URL_FRONTEND + "/projects/" + p.getId();
        String imgUrl = getImageUrl(p.getPicturePath());

        return """
            <table width="100%%" cellpadding="0" cellspacing="0" style="margin-bottom: 15px; border: 1px solid #e0e0e0; border-radius: 8px; overflow: hidden;">
                <tr>
                    <td width="120" style="padding: 0;">
                        <img src="%s" alt="%s" style="width: 120px; height: 120px; object-fit: cover; display: block;">
                    </td>
                    <td style="padding: 15px; vertical-align: top;">
                        <h3 style="margin: 0 0 8px 0; font-size: 18px;">
                            <a href="%s" style="color: #667eea; text-decoration: none; font-weight: bold;">%s</a>
                        </h3>
                        <p style="margin: 0 0 8px 0; font-size: 13px; color: #666; line-height: 1.4;">%s</p>
                        <div style="font-size: 12px; color: #999;">
                            ⏱️ %s | 📊 %s
                        </div>
                    </td>
                </tr>
            </table>
            """.formatted(
                imgUrl,
                p.getTitle(),
                link,
                p.getTitle(),
                p.getDescription() != null && p.getDescription().length() > 100
                        ? p.getDescription().substring(0, 100) + "..."
                        : p.getDescription(),
                p.getTimePrep(),
                p.getCategory() != null ? p.getCategory().getName() : "כללי"
        );
    }

    private String buildChallengeCard(Challenge c) {
        String link = BASE_URL_FRONTEND + "/challenges/" + c.getId();
        String imgUrl = getImageUrl(c.getPicturePath());

        return """
            <table width="100%%" cellpadding="0" cellspacing="0" style="margin-bottom: 15px; background: linear-gradient(135deg, #f093fb 0%%, #f5576c 100%%); border-radius: 8px; overflow: hidden;">
                <tr>
                    <td width="100" style="padding: 0;">
                        <img src="%s" alt="%s" style="width: 100px; height: 100px; object-fit: cover; display: block;">
                    </td>
                    <td style="padding: 15px; color: white;">
                        <h3 style="margin: 0 0 8px 0; font-size: 18px; color: white;">🎯 %s</h3>
                        <p style="margin: 0 0 8px 0; font-size: 13px; opacity: 0.95; line-height: 1.4;">%s</p>
                        <div style="font-size: 12px; opacity: 0.9;">
                            📅 עד: %s
                        </div>
                        <a href="%s" style="display: inline-block; margin-top: 10px; padding: 8px 20px; background-color: white; color: #f5576c; text-decoration: none; border-radius: 20px; font-weight: bold; font-size: 13px;">
                            השתתפי באתגר →
                        </a>
                    </td>
                </tr>
            </table>
            """.formatted(
                imgUrl,
                c.getTheme(),
                c.getTheme(),
                c.getContent() != null && c.getContent().length() > 120
                        ? c.getContent().substring(0, 120) + "..."
                        : c.getContent(),
                c.getEndDate() != null ? c.getEndDate().toString() : "ללא הגבלת זמן",
                link
        );
    }

    private String getImageUrl(String picturePath) {
        if (picturePath == null || picturePath.isEmpty()) {
            return "https://via.placeholder.com/120x120/667eea/ffffff?text=DIY";
        }

        // אם התמונה כבר URL מלא
        if (picturePath.startsWith("http")) {
            return picturePath;
        }

        // אחרת, בנה URL מלא
        return BASE_URL + "/images/" + picturePath;
    }

}
