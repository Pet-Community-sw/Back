package com.example.PetApp.controller;

import com.example.PetApp.config.stomp.CustomPrincipal;
import com.example.PetApp.domain.ChatMessage;
import com.example.PetApp.security.jwt.token.JwtAuthenticationToken;
import com.example.PetApp.service.chat.ChattingService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
public class ChattingController {
    private final ChattingService chattingService;

    @MessageMapping("/chat/message")
    private void message(@Payload ChatMessage chatMessage, Principal principal) {
        System.out.println(principal);
//        Long profileId = Long.valueOf(principal.getName());
        System.out.println("-------------------");

//        chattingService.sendMessage(chatMessage, profileId);

    }
}
