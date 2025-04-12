package com.example.PetApp.controller;

import com.example.PetApp.domain.ChatMessage;
import com.example.PetApp.security.jwt.token.JwtAuthenticationToken;
import com.example.PetApp.service.chat.ChattingService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
public class ChattingController {
    private final ChattingService chattingService;

    @MessageMapping("/chat/message/")
    private void message(@Payload ChatMessage chatMessage, Principal principal) {
        JwtAuthenticationToken token = (JwtAuthenticationToken) principal;
        Long profileId = token.getProfileId();
        chattingService.sendMessage(chatMessage, profileId);

    }
}
