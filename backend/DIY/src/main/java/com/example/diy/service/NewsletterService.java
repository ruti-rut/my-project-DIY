package com.example.diy.service;

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


    public void createAndSendNewsletter(Users user) {
        System.out.println("1️⃣ מתחיל הכנת מייל עבור: " + user.getUserName());

        // 1. שליפת פרויקטים
        List<Project> projects = projectRepository.findTop3ByOrderByCreatedAtDesc();
        System.out.println("2️⃣ מספר הפרויקטים שנמצאו ב-DB: " + projects.size());

        if (projects.isEmpty()) {
            System.out.println("⚠️ אזהרה: לא נמצאו פרויקטים בכלל! המייל לא יישלח.");
            return;
        }

        // 2. הכנת רשימת כותרות ל-AI
        List<String> titles = projects.stream().map(Project::getTitle).toList();
        System.out.println("3️⃣ שולח בקשה ל-AI עם הכותרות: " + titles);

        // 3. יצירת תוכן טקסטואלי ע"י AI
        try {
            String aiContent = aiChatService.generateNewsletterContent(user.getUserName(), titles);
            System.out.println("4️⃣ התקבלה תשובה מה-AI (אורך הטקסט: " + aiContent.length() + ")");

            // 4. הרכבת ה-HTML
            String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
            String subject = "✨ רעיונות חדשים ליצירה מחכים לך! (נשלח ב-" + now + ")";
            String htmlBody = buildHtml(user.getUserName(), aiContent, projects);

            // 5. שליחה
            System.out.println("5️⃣ שולח את המייל בפועל לכתובת: " + user.getMail());
            emailSenderService.send(user.getMail(), subject, htmlBody);
            System.out.println("✅ המייל נשלח בהצלחה!");
        } catch (Exception e) {
            System.err.println("❌ שגיאה בזמן יצירה או שליחת המייל: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private String buildHtml(String userName, String aiText, List<Project> projects) {
        StringBuilder sb = new StringBuilder();
        // ... (קוד HTML ראשוני) ...
        sb.append("<h2 style='color: #444; margin-top: 30px;'>פרויקטים מומלצים:</h2>");
        for (Project p : projects) {

            // 📢 1. תיקון הקישור (מנע 401 Unauthorized):
            // הקישור צריך להוביל לדף הצפייה המלא ב-Frontend (ב-Angular/React).
            String link = BASE_URL_FRONTEND + "/projects/" + p.getId(); // ודאי שהנתיב /project-details הוא הנכון אצלך ב-Frontend

            // 📢 2. תיקון נתיב התמונה (מנע תמונה שבורה):
            // הנתיב המלא עובר דרך ה-ImageController החדש שיצרנו: /images/{filename}
            String imgUrl;
            String picturePath = p.getPicturePath();
            if (picturePath != null && !picturePath.isEmpty()) {
                imgUrl = BASE_URL + "/images/" + picturePath; // הוספת /images/ לנתיב המלא
            } else {
                imgUrl = "https://via.placeholder.com/100";
            }

            sb.append("<div style='border: 1px solid #eee; padding: 10px; margin-bottom: 10px; border-radius: 5px; display: flex; align-items: center;'>");
            // 📢 עדכון השימוש ב-img ל-imgUrl
            sb.append("<img src='").append(imgUrl).append("' style='width: 80px; height: 80px; object-fit: cover; border-radius: 5px; margin-left: 10px;'>");
            sb.append("<div>");
            // 📢 עדכון השימוש ב-link
            sb.append("<h3 style='margin: 0 0 5px 0;'><a href='").append(link).append("' style='color: #ff6b6b; text-decoration: none;'>").append(p.getTitle()).append("</a></h3>");
            sb.append("<span style='font-size: 12px; color: #888;'>").append(p.getTimePrep()).append(" • רמת קושי: ").append(p.getCategory() != null ? p.getCategory().getName() : "כללי").append("</span>");
            sb.append("</div>");
            sb.append("</div>");
        }
        sb.append("<div style='text-align: center; margin-top: 20px; font-size: 12px; color: #aaa;'>נשלח באהבה ע\"י צוות האתר</div>");
        sb.append("</div></div>");
        return sb.toString();
    }
}
