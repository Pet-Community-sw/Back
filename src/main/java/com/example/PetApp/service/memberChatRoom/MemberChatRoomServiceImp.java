package com.example.PetApp.service.memberChatRoom;

import com.example.PetApp.domain.Member;
import com.example.PetApp.domain.MemberChatRoom;
import com.example.PetApp.dto.memberchat.MemberChatRoomsResponseDto;
import com.example.PetApp.repository.jpa.MemberChatRoomRepository;
import com.example.PetApp.repository.jpa.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MemberChatRoomServiceImp implements MemberChatRoomService {

    private final MemberChatRoomRepository memberChatRoomRepository;
    private final MemberRepository memberRepository;
    private final StringRedisTemplate redisTemplate;

    @Transactional
    @Override
    public ResponseEntity<?> getUserChatRooms(String email) {
        Member member = memberRepository.findByEmail(email).get();
        List<MemberChatRoom> memberChatRooms = memberChatRoomRepository.findAllByMembersContains(member);

        List<MemberChatRoomsResponseDto> memberChatRoomsResponseDtos = memberChatRooms.stream()
                .map(memberChatRoom -> {
                    String roomName = filterMember(memberChatRoom.getMembers(), member).getName();
                    String lastMessage = redisTemplate.opsForValue().get("memberChat:lastMessage" + memberChatRoom.getMemberChatRoomId());
                    String count = redisTemplate.opsForValue().get("unReadMemberChat:" + memberChatRoom.getMemberChatRoomId() + ":" + member.getMemberId());
                    String lastMessageTime = redisTemplate.opsForValue().get("memberChat:lastMessageTime:" + memberChatRoom.getMemberChatRoomId());
                            int unReadCount = count != null ? Integer.parseInt(count) : 0;
                            LocalDateTime lastMessageLocalDateTime = null;
                            if (lastMessageTime != null) {
                                lastMessageLocalDateTime = LocalDateTime.parse(lastMessageTime);
                            }
                            return MemberChatRoomsResponseDto.builder()
                                    .chatName(roomName)
                            .lastMessage(lastMessage)
                                    .unReadCount(unReadCount)
                                    .lastMessageTime(lastMessageLocalDateTime)
                                    .build();
                }).collect(Collectors.toList());
        return ResponseEntity.ok(memberChatRoomsResponseDtos);

    }

    private static Member filterMember(List<Member> members, Member member) {
        Member returnMember = null;
        for (Member member1 : members) {
            if (!(member1.equals(member))) {
                returnMember= member1;
            }
        }
        return returnMember;
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
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("memberChatRoomId", newMemberChatRoom.getMemberChatRoomId()));
    }

    @Override
    public ResponseEntity<?> deleteUserChatRoom(Long memberChatRoomId, String email) {
        Member member = memberRepository.findByEmail(email).get();
        Optional<MemberChatRoom> memberChatRoom = memberChatRoomRepository.findById(memberChatRoomId);
        if (memberChatRoom.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("해당 방을 찾을 수 없습니다.");
        }
        if (!(memberChatRoom.get().getMembers().contains(member))) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("권한이 없습니다.");
        }
        memberChatRoomRepository.deleteById(memberChatRoomId);
        return ResponseEntity.ok().body("삭제 완료.");
    }
}
