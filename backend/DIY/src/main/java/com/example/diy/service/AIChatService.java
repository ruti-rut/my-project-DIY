package com.example.diy.service;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.ArrayList;
import java.util.List;

@Service
public class AIChatService {

    private final ChatClient chatClient;
    private final static String SYSTEM_INSTRUCTION= """
            אתה עוזר AI שנועד לעזור בעניני בישולים ומתכונים בלבד.
            .ענה בשפה פשוטה שגם משתמשים ללא ניסיון בבישול יוכלו להבין אותך
           אם יש מוצר חלופי שאמור להתאים למתכון תתן את כל החלופות.
           אם מישהו שואל אותך על מזג אוויר תענה לו גם.
           אם שואלים שאלות שלא קשורות לעניני מתכונים ובישול ענה בנימוס שאתה עוזר רק לעניני בישול.
           """;
    private final ChatMemory chatMemory;

    public AIChatService(ChatClient.Builder chatClient,ChatMemory chatMemory) {
        this.chatClient = chatClient.build();
        this.chatMemory = chatMemory;
    }


    public Flux<String> getResponse(String prompt, String conversationId){
        List<Message> messageList=new ArrayList<>();
        //ההודעה הראשונה - ההנחיה הראשונית
        messageList.add(new SystemMessage(SYSTEM_INSTRUCTION));
        //מוסיפים את כל ההודעות ששייכות לאותה השיחה
        messageList.addAll(chatMemory.get(conversationId));
        //השאלה הנוכחית
        UserMessage userMessage=new UserMessage(prompt);
        messageList.add(userMessage);

        Flux<String> aiResponse=chatClient.prompt().messages(messageList)
                .stream().content();
        //שמירת התגובה בזכרון
        //התגובה של ה-AI
        AssistantMessage aiMessage=new AssistantMessage(aiResponse.toString());
        List<Message> messageList1=List.of(userMessage,aiMessage);
        //מוסיפים לזכרון את השאלה והתשובה
        chatMemory.add(conversationId,messageList1);
        return aiResponse;

    }
    //    public String getResponse(String prompt){
//        SystemMessage systemMessage=new SystemMessage(SYSTEM_INSTRUCTION);
//        UserMessage userMessage=new UserMessage(prompt);
//
//        List<Message> messageList= List.of(systemMessage,userMessage);
//
//        return chatClient.prompt().messages(messageList).call().content();
//    }


}
