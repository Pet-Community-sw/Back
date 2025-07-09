package com.example.PetApp.service.chatting;

import com.example.PetApp.domain.ChatMessage;
import org.springframework.stereotype.Service;

@Service
public interface ChattingService {
    void sendToMessage(ChatMessage chatMessage, Long id);
}
