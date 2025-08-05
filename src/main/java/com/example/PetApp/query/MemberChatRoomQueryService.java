package com.example.PetApp.query;

import com.example.PetApp.domain.MemberChatRoom;
import com.example.PetApp.exception.NotFoundException;
import com.example.PetApp.repository.jpa.MemberChatRoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MemberChatRoomQueryService {

    private final MemberChatRoomRepository memberChatRoomRepository;

    public MemberChatRoom findByMemberChatRoom(Long memberChatRoomId) {
        return memberChatRoomRepository.findById(memberChatRoomId).orElseThrow(() -> new NotFoundException("해당 채팅방은 없습니다."));
    }
}
