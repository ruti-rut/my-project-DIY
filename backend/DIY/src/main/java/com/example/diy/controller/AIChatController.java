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
            System.out.println("📨 Controller received: " + message);
            return aiChatService.getResponse(message, conversationId)
                    .doOnNext(chunk -> System.out.println("📤 Sending chunk: " +
                            (chunk.length() > 50 ? chunk.substring(0, 50) + "..." : chunk)))
                    .doOnComplete(() -> System.out.println("✅ Stream completed"))
                    .doOnError(e -> System.err.println("❌ Stream error: " + e.getMessage()));
        } catch (Exception e) {
            System.err.println("❌ Controller error: " + e.getMessage());
            e.printStackTrace();
            return Flux.error(e);
        }
    }
}
