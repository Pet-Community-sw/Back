package com.example.PetApp.service.chatting.handler;

import com.example.PetApp.domain.ChatMessage;
import org.springframework.stereotype.Service;

@Service
public interface ChatRoomHandler {
    void handleGroupChat(ChatMessage chatMessage, Long senderId);

    void handleOneToOneChat(ChatMessage chatMessage, Long senderId);
}
