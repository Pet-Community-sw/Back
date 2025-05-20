package com.example.PetApp.controller;

import com.example.PetApp.domain.ChatMessage;
import com.example.PetApp.dto.groupchat.UpdateChatRoomDto;
import com.example.PetApp.security.jwt.token.JwtAuthenticationToken;
import com.example.PetApp.service.chat.ChatRoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import static com.example.PetApp.domain.ChatMessage.*;


@RestController
@RequiredArgsConstructor
@RequestMapping("/chat-rooms")
public class ChatRoomController {

    private final ChatRoomService chatRoomService;
    //변경해야됩니다. jwt토큰에 profileId집어 넣었어요
    @GetMapping()
    private ResponseEntity<?> chatRoomList(Authentication authentication) {
        Long profileId = getProfileId( authentication);
        return chatRoomService.getChatRooms(profileId);
    }

    @GetMapping("/{chatRoomId}")
    private ResponseEntity<?> getMessages(@PathVariable Long chatRoomId,
                                          @RequestParam(defaultValue = "0") int page,
                                          Authentication authentication
    ) {
        Long profileId = getProfileId( authentication);
        return chatRoomService.getMessages(chatRoomId, profileId, page);
    }

    @PutMapping("/{chatRoomId}")
    private ResponseEntity<?> updateChatRoom(@PathVariable Long chatRoomId, @RequestBody UpdateChatRoomDto updateChatRoomDto, Authentication authentication) {
        Long profileId = getProfileId( authentication);
        return chatRoomService.updateChatRoom(chatRoomId, updateChatRoomDto, profileId);
    }

    @DeleteMapping("/{chatRoomId}")
    private ResponseEntity<?> deleteChatRoom(@PathVariable Long chatRoomId, Authentication authentication) {
        Long profileId = getProfileId(authentication);
        return chatRoomService.deleteChatRoom(chatRoomId, profileId);
    }

    private static Long getProfileId(Authentication authentication) {

        JwtAuthenticationToken authentication1 = (JwtAuthenticationToken) authentication;
        return Long.valueOf(authentication1.getProfileId().toString());
    }
}
