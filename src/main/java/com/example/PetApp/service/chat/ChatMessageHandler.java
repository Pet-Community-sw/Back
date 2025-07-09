package com.example.PetApp.service.chat;

import com.example.PetApp.domain.ChatMessage;
import org.springframework.stereotype.Service;

@Service
public interface ChatMessageHandler {
    void handleEnterMessage(ChatMessage chatMessage);

    void handleLeaveMessage(ChatMessage chatMessage, Long senderId);

    void handleTalkMessage(ChatMessage chatMessage);
}
