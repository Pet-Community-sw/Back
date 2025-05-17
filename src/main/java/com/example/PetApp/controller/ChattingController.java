package com.example.PetApp.controller;

import com.example.PetApp.domain.ChatMessage;
import com.example.PetApp.service.chat.ChattingService;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
public class ChattingController {
    private final ChattingService chattingService;

    @MessageMapping("/chat/message")
    public void message(@Payload ChatMessage chatMessage, Principal principal) throws JsonProcessingException {//memberId or profileId
        Long id = Long.valueOf(principal.getName());
        chattingService.sendToMessage(chatMessage, id);
    }
}

