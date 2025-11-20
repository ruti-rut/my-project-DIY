package com.example.diy.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;

public class AIService {
    @Value("${my.ai.apiKey}")
    private String apiKey;

    private final RestClient restClient = RestClient.builder().build();

    public String generateDailyContent() {

        String prompt = """
                צור תוכן קצר, מקצועי ונעים למייל יומי. 
                הטון: חכם, אנושי, מועיל.
                התוכן: טיפ יומי פרקטי ושימושי.
                """;

        Map<String, Object> requestBody = Map.of(
                "model", "gpt-5-mini",
                "messages", List.of(
                        Map.of("role", "user", "content", prompt)
                )
        );

        Map response = restClient.post()
                .uri("https://api.openai.com/v1/chat/completions")
                .header("Authorization", "Bearer " + apiKey)
                .body(requestBody)
                .retrieve()
                .body(Map.class);

        return ((Map)((List)response.get("choices")).get(0))
                .get("message").toString();
    }

}
