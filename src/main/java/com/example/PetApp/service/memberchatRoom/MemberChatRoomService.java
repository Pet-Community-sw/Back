package com.example.PetApp.service.memberchatRoom;

import com.example.PetApp.domain.Member;
import com.example.PetApp.dto.groupchat.ChatMessageResponseDto;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public interface MemberChatRoomService {
    ResponseEntity<?> getMemberChatRooms(String email);

    ResponseEntity<?> createMemberChatRoom(Member fromMember, Member member);

    ResponseEntity<?> createMemberChatRoom(Long memberId, String email);

    ResponseEntity<?> updateMemberChatRoom(Long memberChatRoomId, String userChatRoomName, String email);

    ResponseEntity<?> deleteMemberChatRoom(Long userChatRoomId, String email);

    ChatMessageResponseDto getMessages(Long memberChatRoomId, String email, int page);

}
