package com.example.PetApp.service.memberChatRoom;

import com.example.PetApp.domain.Member;
import com.example.PetApp.domain.MemberChatRoom;
import com.example.PetApp.repository.jpa.MemberChatRoomRepository;
import com.example.PetApp.repository.jpa.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MemberChatRoomServiceImp implements MemberChatRoomService {

    private final MemberChatRoomRepository memberChatRoomRepository;
    private final MemberRepository memberRepository;

    @Transactional
    @Override
    public ResponseEntity<?> getUserChatRooms(String email) {
        return null;
    }

    @Transactional
    @Override
    public ResponseEntity<?> createUserChatRoom(Long memberId, String email) {
        Optional<Member> fromMember = memberRepository.findById(memberId);
        if (fromMember.isEmpty()) {
            return ResponseEntity.status(HttpStatus.FOUND).body("해당 유저를 찾을 수 없습니다.");
        }
        Member member = memberRepository.findByEmail(email).get();
        if (memberChatRoomRepository.existsByMembers(fromMember.get(), member)) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("이미 있는 방입니다");
        }

        List<Member> members = new ArrayList<>();
        members.add(fromMember.get());
        members.add(member);
        MemberChatRoom memberChatRoom = MemberChatRoom.builder()
                .members(members)
                .build();
        MemberChatRoom newMemberChatRoom = memberChatRoomRepository.save(memberChatRoom);
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("memberChatRoomId", newMemberChatRoom.getUserChatRoomId()));
    }

    @Override
    public ResponseEntity<?> updateUserChatRoom(String userChatRoomName, String email) {
        return null;
    }

    @Override
    public ResponseEntity<?> deleteUserChatRoom(Long userChatRoomId, String email) {
        return null;
    }
}
