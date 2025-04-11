package com.example.PetApp.controller;

import com.example.PetApp.dto.chat.CreateChatRoomDto;
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
        JwtAuthenticationToken jwtAuthenticationToken = (JwtAuthenticationToken) authentication;
        String email = jwtAuthenticationToken.getPrincipal().toString();
        Long profileId = jwtAuthenticationToken.getProfileId();
        return chatRoomService.getChatRoomList(profileId, email);
    }

    @GetMapping("/{chatRoomId}")
    private ResponseEntity<?> getMessages(@PathVariable Long chatRoomId,
                                          @RequestParam(defaultValue = "0") int page,
                                          Authentication authentication
    ) {
        JwtAuthenticationToken jwtAuthenticationToken = (JwtAuthenticationToken) authentication;
        String email = jwtAuthenticationToken.getPrincipal().toString();
        Long profileId = jwtAuthenticationToken.getProfileId();
        return chatRoomService.getMessages(chatRoomId, profileId, email, page);
    }


    @PostMapping()
    private ResponseEntity<?> createChatRoom(@RequestBody CreateChatRoomDto createChatRoomDto, Authentication authentication) {
        JwtAuthenticationToken jwtAuthenticationToken = (JwtAuthenticationToken) authentication;
        String email = jwtAuthenticationToken.getPrincipal().toString();
        Long profileId = jwtAuthenticationToken.getProfileId();
        return chatRoomService.createChatRoom(createChatRoomDto, profileId, email);
    }

    @DeleteMapping("/{chatRoomId}")
    private ResponseEntity<?> deleteChatRoom(@PathVariable Long chatRoomId,
                                             Authentication authentication) {
        JwtAuthenticationToken jwtAuthenticationToken = (JwtAuthenticationToken) authentication;
        String email = jwtAuthenticationToken.getPrincipal().toString();
        Long profileId = jwtAuthenticationToken.getProfileId();
        return chatRoomService.deleteChatRoom(chatRoomId, profileId, email);
    }

    @PutMapping()
    private ResponseEntity<?> updateChatRoom(@RequestBody UpdateChatRoomDto updateChatRoomDto, Authentication authentication) {
        JwtAuthenticationToken jwtAuthenticationToken = (JwtAuthenticationToken) authentication;
        String email = jwtAuthenticationToken.getPrincipal().toString();
        Long profileId = jwtAuthenticationToken.getProfileId();
        return chatRoomService.updateChatRoom(updateChatRoomDto, profileId, email);
    }
}
