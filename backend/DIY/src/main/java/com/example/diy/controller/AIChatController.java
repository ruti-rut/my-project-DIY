package com.example.diy.controller;

import com.example.diy.service.AIChatService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/api/AIAssistant")
public class AIChatController {
    AIChatService aiChatService;

    public AIChatController(AIChatService aiChatService) {
        this.aiChatService = aiChatService;
    }

    @GetMapping(value = "/chat", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> getResponse(
            @RequestParam String message,
            @RequestParam String conversationId) {
        try {
            // קריאה לשירות עם הפרמטרים החדשים
            // כמובן, תצטרכי לשנות את חתימת המתודה ב-AIChatService בהתאם
            return aiChatService.getResponse(message, conversationId);
        } catch (Exception e) {
            // ב-Flux קצת יותר מורכב לטפל בשגיאות HTTP, לרוב מחזירים שגיאה בתוך ה-Stream
            // אבל לצורך הדוגמה, נחזיר Flux ריק ונשתמש בהדפסת השגיאה
            e.printStackTrace();
            return Flux.error(e);
        }
    }

}
