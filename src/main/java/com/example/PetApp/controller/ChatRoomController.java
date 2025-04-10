package com.example.PetApp.controller;

import com.example.PetApp.dto.chat.CreateChatRoomDto;
import com.example.PetApp.dto.chat.UpdateChatRoomDto;
import com.example.PetApp.service.chat.ChatRoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;


@RestController
@RequiredArgsConstructor
@RequestMapping("/chat-rooms")
public class ChatRoomController {

    private final ChatRoomService chatRoomService;

    @GetMapping("/{profileId}")
    private ResponseEntity<?> chatRoomList(@PathVariable Long profileId, Authentication authentication) {
        String email = authentication.getPrincipal().toString();
        return chatRoomService.getChatRoomList(profileId, email);
    }

    @PostMapping()
    private ResponseEntity<?> createChatRoom(@RequestBody CreateChatRoomDto createChatRoomDto, Authentication authentication) {
        String email = authentication.getPrincipal().toString();
        return chatRoomService.createChatRoom(createChatRoomDto, email);
    }

    @DeleteMapping("/{chatRoomId}")
    private ResponseEntity<?> deleteChatRoom(@PathVariable Long chatRoomId,
                                             @RequestParam Long profileId,
                                             Authentication authentication) {
        String email = authentication.getPrincipal().toString();
        return chatRoomService.deleteChatRoom(chatRoomId, profileId, email);
    }

    @PutMapping()
    private ResponseEntity<?> updateChatRoom(@RequestBody UpdateChatRoomDto updateChatRoomDto, Authentication authentication) {
        String email = authentication.getPrincipal().toString();
        return chatRoomService.updateChatRoom(updateChatRoomDto, email);
    }
}
