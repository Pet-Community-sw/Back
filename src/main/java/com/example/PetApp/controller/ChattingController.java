package com.example.PetApp.controller;

import com.example.PetApp.domain.ChatMessage;
import com.example.PetApp.service.chat.ChatRoomService;
import com.example.PetApp.service.chat.ChattingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class ChattingController {
    private final ChattingService chattingService;
    private final ChatRoomService chatRoomService;

    @GetMapping("/chat/messages/{chatRoomId}")
    private ResponseEntity<?> getMessages(@PathVariable Long chatRoomId,
                                          @RequestParam(defaultValue = "0") int page,
                                          @RequestParam Long profileId,
                                          Authentication authentication
                                          ) {
        String email = authentication.getPrincipal().toString();
        return chatRoomService.getMessages(chatRoomId, profileId, email, page);
    }

    @MessageMapping("/chat/message")
    private void message(ChatMessage chatMessage) {
        chattingService.sendMessage(chatMessage);
    }
}
