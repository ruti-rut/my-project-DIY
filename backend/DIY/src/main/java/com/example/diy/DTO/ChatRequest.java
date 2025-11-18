package com.example.diy.DTO;

public record ChatRequest(String message,String conversationId) {
    public ChatRequest{
        if(conversationId==null ||conversationId.isBlank()){
            conversationId="default-user";
        }
    }
}
