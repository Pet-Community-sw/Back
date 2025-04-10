package com.example.PetApp.service;

import com.example.PetApp.domain.ChatRoom;
import com.example.PetApp.domain.Member;
import com.example.PetApp.domain.Post;
import com.example.PetApp.domain.Profile;
import com.example.PetApp.dto.ChatRoomResponseDto;
import com.example.PetApp.dto.CreateChatRoomDto;
import com.example.PetApp.dto.UpdateChatRoomDto;
import com.example.PetApp.repository.ChatRoomRepository;
import com.example.PetApp.repository.MemberRepository;
import com.example.PetApp.repository.PostRepository;
import com.example.PetApp.repository.ProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatRoomServiceImp implements ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;
    private final ProfileRepository profileRepository;
    private final MemberRepository memberRepository;
    private final PostRepository postRepository;

    @Transactional
    @Override
    public ResponseEntity<?> getChatRoomList(Long profileId, String email) {
        Member member = memberRepository.findByEmail(email).get();
        Optional<Profile> profile = profileRepository.findById(profileId);
        if (profile.isEmpty()||!(profile.get().getMemberId().equals(member.getMemberId()))) {
            return ResponseEntity.badRequest().body("잘못된 요청입니다.");
        }
        Set<ChatRoom> chatRoomList = chatRoomRepository.findAllByProfilesContains(profile.get());
        //redis 정보가지고와야됨.
        List<ChatRoomResponseDto> chatRoomResponseDto=chatRoomList.stream().map(ChatRoomResponseDto::from)
                .collect(Collectors.toList());
        return ResponseEntity.ok(chatRoomResponseDto);
    }

    @Transactional
    @Override
    public ResponseEntity<?> createChatRoom(CreateChatRoomDto createChatRoomDto, String email) {
        Member member = memberRepository.findByEmail(email).get();
        Optional<Profile> profile = profileRepository.findById(createChatRoomDto.getProfileId());
        Optional<Post> post = postRepository.findById(createChatRoomDto.getPostId());
        if (profile.isEmpty()||post.isEmpty()|| !(profile.get().getMemberId().equals(member.getMemberId()))) {
            return ResponseEntity.badRequest().body("잘못된 요청입니다.");
        }
        Optional<ChatRoom> chatRoom2 = chatRoomRepository.findByPost(post.get());
        if (chatRoom2.isEmpty()) {//채팅방이 없으면 새로운생성 있으면 profiles에 신청자 Profile 추가
            ChatRoom chatRoom = ChatRoom.builder()
                    .name(post.get().getProfile().getDogName()+"님의 방")
                    .limitCount(createChatRoomDto.getLimitCount())//나중에 게시물에서 인원 수를 고정.
                    .post(post.get())
                    //이게 수정에서 가능하려나?
                    .build();
            chatRoom.addProfiles(profile.get());//신청하는사람.
            chatRoom.addProfiles(post.get().getProfile());//글 작성자.
            ChatRoom chatRoom1 = chatRoomRepository.save(chatRoom);
            return ResponseEntity.status(HttpStatus.CREATED).body(chatRoom1.getChatRoomId());
        }else {
            ChatRoom chatRoom = chatRoom2.get();
            Set<Profile> profiles = chatRoom.getProfiles();
            profiles.add(profile.get());
            chatRoom.setProfiles(profiles);
            return ResponseEntity.ok().build();
        }
    }

    @Transactional
    @Override
    public ResponseEntity<?> deleteChatRoom(Long chatRoomId, Long profileId, String email) {
        Optional<ChatRoom> chatRoom = chatRoomRepository.findById(chatRoomId);
        Member member = memberRepository.findByEmail(email).get();
        Optional<Profile> profile = profileRepository.findById(profileId);
        if (chatRoom.isEmpty() || !(profile.get().getMemberId().equals(member.getMemberId()))) {
            return ResponseEntity.badRequest().body("잘못된 요청입니다.");
        }
        ChatRoom chatRoom1 = chatRoom.get();
        Set<Profile> profiles = chatRoom1.getProfiles();
        profiles.remove(profile.get());
        chatRoom1.setProfiles(profiles);//방 사용자 수가 1이되면 채팅방 전체 삭제.
        if (chatRoomRepository.countByProfile(chatRoomId) == 1) {
            chatRoomRepository.deleteById(chatRoomId);
        }
        return ResponseEntity.ok().build();
    }

    @Transactional
    @Override//방장만 수정할 수 있도록 설정.
    public ResponseEntity<?> updateChatRoom(UpdateChatRoomDto updateChatRoomDto, String email) {
        Optional<ChatRoom> chatRoom = chatRoomRepository.findById(updateChatRoomDto.getChatRoomId());
        Optional<Profile> profile = profileRepository.findById(updateChatRoomDto.getProfileId());
        Member member = memberRepository.findByEmail(email).get();
        if (chatRoom.isEmpty() || !(profile.get().getMemberId().equals(member.getMemberId()))) {
            return ResponseEntity.badRequest().body("잘못된 요청입니다.");
        }
        if (!(chatRoom.get().getPost().getProfile().getProfileId().equals(profile.get().getProfileId()))) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("수정 권한이 없습니다.");
        }
        ChatRoom chatRoom1 = chatRoom.get();
        chatRoom1.setName(updateChatRoomDto.getChatRoomTitle());
        chatRoom1.setLimitCount(updateChatRoomDto.getLimitCount());
        return ResponseEntity.ok().build();
    }

}
