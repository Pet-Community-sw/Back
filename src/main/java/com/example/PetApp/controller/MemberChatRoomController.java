package com.example.PetApp.controller;

import com.example.PetApp.service.memberChatRoom.MemberChatRoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/member-chat-rooms")
public class MemberChatRoomController {
    private final MemberChatRoomService memberChatRoomService;

    @GetMapping
    public ResponseEntity<?> getUserChatRooms(Authentication authentication) {
        return memberChatRoomService.getUserChatRooms(getEmail(authentication));
    }

    @PostMapping
    public ResponseEntity<?> createUserChatRoom(@RequestBody Long memberId, Authentication authentication) {
        return memberChatRoomService.createUserChatRoom(memberId, getEmail(authentication));
    }

    @PutMapping("/{memberChatRoomId}")
    public ResponseEntity<?> updateUserChatRoom(@PathVariable Long memberChatRoomId, @RequestBody String memberChatRoomName, Authentication authentication) {
        return memberChatRoomService.updateUserChatRoom(memberChatRoomId, memberChatRoomName, getEmail(authentication));
    }

    @DeleteMapping("/{memberChatRoomId}")
    public ResponseEntity<?> deleteUserChatRoom(@PathVariable Long memberChatRoomId, Authentication authentication) {
        return memberChatRoomService.deleteUserChatRoom(memberChatRoomId, getEmail(authentication));
    }

    private static String getEmail(Authentication authentication) {
        return authentication.getPrincipal().toString();
    }
}
