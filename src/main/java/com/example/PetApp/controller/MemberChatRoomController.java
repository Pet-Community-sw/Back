package com.example.PetApp.controller;

import com.example.PetApp.dto.MessageResponse;
import com.example.PetApp.dto.groupchat.ChatMessageResponseDto;
import com.example.PetApp.dto.memberchat.CreateMemberChatRoomResponseDto;
import com.example.PetApp.dto.memberchat.MemberChatRoomsResponseDto;
import com.example.PetApp.service.memberchatRoom.MemberChatRoomService;
import com.example.PetApp.util.AuthUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/member-chat-rooms")
public class MemberChatRoomController {
    private final MemberChatRoomService memberChatRoomService;

    @GetMapping
    public List<MemberChatRoomsResponseDto> getMemberChatRooms(Authentication authentication) {
        return memberChatRoomService.getMemberChatRooms(AuthUtil.getEmail(authentication));
    }

    @PostMapping
    public CreateMemberChatRoomResponseDto createMemberChatRoom(@RequestBody Long memberId, Authentication authentication) {
        return memberChatRoomService.createMemberChatRoom(memberId, AuthUtil.getEmail(authentication));
    }

    @PutMapping("/{memberChatRoomId}")
    public ResponseEntity<?> updateMemberChatRoom(@PathVariable Long memberChatRoomId, @RequestBody String memberChatRoomName, Authentication authentication) {
        memberChatRoomService.updateMemberChatRoom(memberChatRoomId, memberChatRoomName, AuthUtil.getEmail(authentication));
        return ResponseEntity.ok(new MessageResponse("수정 되었습니다."));
    }

    @DeleteMapping("/{memberChatRoomId}")
    public ResponseEntity<?> deleteMemberChatRoom(@PathVariable Long memberChatRoomId, Authentication authentication) {
        memberChatRoomService.deleteMemberChatRoom(memberChatRoomId, AuthUtil.getEmail(authentication));
        return ResponseEntity.ok(new MessageResponse("삭제 되었습니다."));
    }

    @GetMapping("/{memberChatRoomId}")
    public ChatMessageResponseDto getMessages(@PathVariable Long memberChatRoomId,
                                              @RequestParam(defaultValue ="0") int page,
                                              Authentication authentication) {
        return memberChatRoomService.getMessages(memberChatRoomId, AuthUtil.getEmail(authentication), page);
    }
}
