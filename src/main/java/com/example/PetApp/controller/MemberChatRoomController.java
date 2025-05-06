package com.example.PetApp.controller;

import com.example.PetApp.service.memberChatRoom.MemberChatRoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user-chat-rooms")
public class MemberChatRoomController {
    private final MemberChatRoomService memberChatRoomService;

    @GetMapping
    public ResponseEntity<?> getUserChatRooms(Authentication authentication) {
        String email = authentication.getPrincipal().toString();
        return memberChatRoomService.getUserChatRooms(email);
    }

    @PostMapping
    public ResponseEntity<?> createUserChatRoom(@RequestBody Long memberId, Authentication authentication) {
        String email = authentication.getPrincipal().toString();
        return memberChatRoomService.createUserChatRoom(memberId, email);
    }

    @PutMapping
    public ResponseEntity<?> updateUserChatRoom(@RequestBody String userChatRoomName, Authentication authentication) {
        String email = authentication.getPrincipal().toString();
        return memberChatRoomService.updateUserChatRoom(userChatRoomName, email);
    }

    @DeleteMapping("/{userChatRoomId}")
    public ResponseEntity<?> deleteUserChatRoom(@PathVariable Long userChatRoomId, Authentication authentication) {
        String email = authentication.getPrincipal().toString();
        return memberChatRoomService.deleteUserChatRoom(userChatRoomId, email);
    }
}
