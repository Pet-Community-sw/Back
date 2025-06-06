package com.example.PetApp.service.memberchatRoom;

import com.example.PetApp.domain.ChatMessage;
import com.example.PetApp.domain.Member;
import com.example.PetApp.domain.MemberChatRoom;
import com.example.PetApp.dto.groupchat.ChatMessageResponseDto;
import com.example.PetApp.dto.memberchat.CreateMemberChatRoomResponseDto;
import com.example.PetApp.dto.memberchat.MemberChatRoomsResponseDto;
import com.example.PetApp.exception.ConflictException;
import com.example.PetApp.exception.ForbiddenException;
import com.example.PetApp.exception.NotFoundException;
import com.example.PetApp.mapper.MemberChatRoomMapper;
import com.example.PetApp.repository.jpa.MemberChatRoomRepository;
import com.example.PetApp.repository.jpa.MemberRepository;
import com.example.PetApp.service.chat.ChattingReader;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor//리펙토링 필수
@Slf4j
public class MemberChatRoomServiceImp implements MemberChatRoomService {

    private final MemberChatRoomRepository memberChatRoomRepository;
    private final MemberRepository memberRepository;
    private final StringRedisTemplate redisTemplate;
    private final ChattingReader chattingReader;

    @Transactional(readOnly = true)
    @Override
    public List<MemberChatRoomsResponseDto> getMemberChatRooms(String email) {
        Member member = memberRepository.findByEmail(email).get();
        List<MemberChatRoom> memberChatRooms = memberChatRoomRepository.findAllByMembersContains(member);

        return memberChatRooms.stream()
                .map(memberChatRoom -> {
                    Member anotherMember = filterMember(memberChatRoom.getMembers(), member);
                    String roomName = anotherMember.getName();
                    String roomImageUrl = anotherMember.getMemberImageUrl();
                    String lastMessage = redisTemplate.opsForValue().get("memberChat:lastMessage" + memberChatRoom.getMemberChatRoomId());
                    String count = redisTemplate.opsForValue().get("unReadMemberChat:" + memberChatRoom.getMemberChatRoomId() + ":" + member.getMemberId());
                    String lastMessageTime = redisTemplate.opsForValue().get("memberChat:lastMessageTime:" + memberChatRoom.getMemberChatRoomId());
                    return MemberChatRoomMapper.toMemberChatRoomsResponseDto(roomName, roomImageUrl, lastMessage, count, lastMessageTime);
                }).collect(Collectors.toList());
    }

    @Transactional
    @Override
    public CreateMemberChatRoomResponseDto createMemberChatRoom(Member fromMember, Member member) {//방 제목을 어떻게 할까 대리 산책자 구인했을 때 채팅방
        MemberChatRoom memberChatRoom = getMemberChatRoom(fromMember, member);
        memberChatRoomRepository.save(memberChatRoom);
        return new CreateMemberChatRoomResponseDto(memberChatRoom.getMemberChatRoomId());
    }

    @Transactional
    @Override
    public CreateMemberChatRoomResponseDto createMemberChatRoom(Long memberId, String email) {
        Member fromMember = memberRepository.findById(memberId)
                .orElseThrow(() -> new NotFoundException("해당 유저를 찾을 수 없습니다."));
        Member member = memberRepository.findByEmail(email).get();
        if (memberChatRoomRepository.existsByMembers(fromMember, member)) {
            throw new ConflictException("이미 있는 방입니다.");
        }
        MemberChatRoom memberChatRoom = getMemberChatRoom(fromMember, member);
        MemberChatRoom newMemberChatRoom = memberChatRoomRepository.save(memberChatRoom);
        return new CreateMemberChatRoomResponseDto(newMemberChatRoom.getMemberChatRoomId());
    }

    private static MemberChatRoom getMemberChatRoom(Member fromMember, Member member) {
        List<Member> members = new ArrayList<>();
        members.add(fromMember);
        members.add(member);
        return MemberChatRoom.builder()
                .members(members)
                .build();
    }

    @Transactional
    @Override
    public void updateMemberChatRoom(Long memberChatRoomId, String userChatRoomName, String email) {
        //여기에 무엇을
    }

    @Transactional
    @Override
    public void deleteMemberChatRoom(Long memberChatRoomId, String email) {
        Member member = memberRepository.findByEmail(email).get();
        MemberChatRoom memberChatRoom = memberChatRoomRepository.findById(memberChatRoomId)
                .orElseThrow(() -> new NotFoundException("해당 채팅방을 찾을 수 없습니다."));
        if (!(memberChatRoom.getMembers().contains(member))) {
            throw new ForbiddenException("권한이 없습니다.");
        }
        memberChatRoomRepository.deleteById(memberChatRoomId);
    }

    @Transactional(readOnly = true)
    @Override
    public ChatMessageResponseDto getMessages(Long memberChatRoomId, String email, int page) {
        Member member = memberRepository.findByEmail(email).get();
        return chattingReader.getMessages(memberChatRoomId, member.getMemberId(), ChatMessage.ChatRoomType.ONE, page);
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
}
