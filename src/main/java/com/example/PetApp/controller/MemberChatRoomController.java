package com.example.PetApp.controller;

import com.example.PetApp.service.memberchatRoom.MemberChatRoomService;
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
    public ResponseEntity<?> getMemberChatRooms(Authentication authentication) {
        return memberChatRoomService.getMemberChatRooms(getEmail(authentication));
    }

    @PostMapping
    public ResponseEntity<?> createMemberChatRoom(@RequestBody Long memberId, Authentication authentication) {
        return memberChatRoomService.createMemberChatRoom(memberId, getEmail(authentication));
    }

    @PutMapping("/{memberChatRoomId}")
    public ResponseEntity<?> updateMemberChatRoom(@PathVariable Long memberChatRoomId, @RequestBody String memberChatRoomName, Authentication authentication) {
        return memberChatRoomService.updateMemberChatRoom(memberChatRoomId, memberChatRoomName, getEmail(authentication));
    }

    @DeleteMapping("/{memberChatRoomId}")
    public ResponseEntity<?> deleteMemberChatRoom(@PathVariable Long memberChatRoomId, Authentication authentication) {
        return memberChatRoomService.deleteMemberChatRoom(memberChatRoomId, getEmail(authentication));
    }

    @GetMapping("/{memberChatRoomId}")
    public ResponseEntity<?> getMessages(@PathVariable Long memberChatRoomId,
                                         @RequestParam(defaultValue ="0") int page,
                                         Authentication authentication) {
        return memberChatRoomService.getMessages(memberChatRoomId, getEmail(authentication), page);
    }

    private static String getEmail(Authentication authentication) {
        return authentication.getPrincipal().toString();
    }
}
