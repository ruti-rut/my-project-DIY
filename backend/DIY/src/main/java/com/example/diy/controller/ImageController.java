package com.example.diy.controller;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@RequestMapping("/images")
public class ImageController {

    // שימי לב: הנתיב הזה זהה לנתיב ה-UPLOAD_DIRECTORY ב-ImageUtils
    private static final String UPLOAD_DIRECTORY =System.getProperty("user.dir") + "\\images\\";

    @GetMapping("/{filename:.+}")
    public ResponseEntity<Resource> serveFile(@PathVariable String filename) {
        try {
            Path file = Paths.get(UPLOAD_DIRECTORY).resolve(filename).normalize();
            Resource resource = new UrlResource(file.toUri());

            if (!resource.exists() || !resource.isReadable()) {
                return ResponseEntity.notFound().build();
            }

            // זיהוי אוטומטי של סוג הקובץ לפי הסיומת
            String contentType = determineContentType(filename);

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CACHE_CONTROL, "max-age=3600") // cache למשך שעה
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resource.getFilename() + "\"")
                    .body(resource);

        } catch (MalformedURLException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * קובע את ה-Content-Type לפי סיומת הקובץ
     */
    private String determineContentType(String filename) {
        String lowerFilename = filename.toLowerCase();

        if (lowerFilename.endsWith(".png")) {
            return "image/png";
        } else if (lowerFilename.endsWith(".jpg") || lowerFilename.endsWith(".jpeg")) {
            return "image/jpeg";
        } else if (lowerFilename.endsWith(".gif")) {
            return "image/gif";
        } else if (lowerFilename.endsWith(".webp")) {
            return "image/webp";
        } else if (lowerFilename.endsWith(".svg")) {
            return "image/svg+xml";
        }

        return "image/jpeg"; // ברירת מחדל
    }

    /**
     * בדיקת קיום תמונה
     */
    @GetMapping("/exists/{filename:.+}")
    public ResponseEntity<Boolean> checkImageExists(@PathVariable String filename) {
        try {
            Path file = Paths.get(UPLOAD_DIRECTORY).resolve(filename).normalize();
            Resource resource = new UrlResource(file.toUri());
            return ResponseEntity.ok(resource.exists() && resource.isReadable());
        } catch (MalformedURLException e) {
            return ResponseEntity.ok(false);
        }
    }
}