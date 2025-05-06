package com.example.PetApp.controller;

import com.example.PetApp.dto.chat.UpdateChatRoomDto;
import com.example.PetApp.security.jwt.token.JwtAuthenticationToken;
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
    //변경해야됩니다. jwt토큰에 profileId집어 넣었어요
    @GetMapping()
    private ResponseEntity<?> chatRoomList(Authentication authentication) {
        Long profileId = getProfileId( authentication);
        return chatRoomService.getChatRoomList(profileId);
    }

    @GetMapping("/{chatRoomId}")
    private ResponseEntity<?> getMessages(@PathVariable Long chatRoomId,
                                          @RequestParam(defaultValue = "0") int page,
                                          Authentication authentication
    ) {
        Long profileId = getProfileId( authentication);
        return chatRoomService.getMessages(chatRoomId, profileId, page);
    }

    @PutMapping()
    private ResponseEntity<?> updateChatRoom(@RequestBody UpdateChatRoomDto updateChatRoomDto, Authentication authentication) {
        Long profileId = getProfileId( authentication);
        return chatRoomService.updateChatRoom(updateChatRoomDto, profileId);
    }

    private static Long getProfileId(Authentication authentication) {
        JwtAuthenticationToken authentication1 = (JwtAuthenticationToken) authentication;
        return Long.valueOf(authentication1.getProfileId().toString());
    }
}
