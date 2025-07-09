package com.example.PetApp.controller;

import com.example.PetApp.domain.ChatMessage;
import com.example.PetApp.service.chatting.ChattingService;
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
    public void message(@Payload ChatMessage chatMessage, Principal principal) {//memberId or profileId
        chattingService.sendToMessage(chatMessage, Long.valueOf(principal.getName()));
    }
}

