package com.example.PetApp.service.memberchatRoom;

import com.example.PetApp.domain.Member;
import com.example.PetApp.dto.groupchat.ChatMessageResponseDto;
import com.example.PetApp.dto.memberchat.CreateMemberChatRoomResponseDto;
import com.example.PetApp.dto.memberchat.MemberChatRoomsResponseDto;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface MemberChatRoomService {
    List<MemberChatRoomsResponseDto> getMemberChatRooms(String email);

    CreateMemberChatRoomResponseDto createMemberChatRoom(Member fromMember, Member member);

    CreateMemberChatRoomResponseDto createMemberChatRoom(Long memberId, String email);

    void updateMemberChatRoom(Long memberChatRoomId, String userChatRoomName, String email);

    void deleteMemberChatRoom(Long userChatRoomId, String email);

    ChatMessageResponseDto getMessages(Long memberChatRoomId, String email, int page);

}
