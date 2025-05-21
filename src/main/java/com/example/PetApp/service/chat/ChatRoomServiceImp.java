package com.example.PetApp.service.chat;

import com.example.PetApp.domain.*;
import com.example.PetApp.dto.groupchat.*;
import com.example.PetApp.repository.jpa.ChatRoomRepository;
import com.example.PetApp.repository.jpa.MemberChatRoomRepository;
import com.example.PetApp.repository.jpa.MemberRepository;
import com.example.PetApp.repository.jpa.ProfileRepository;
import com.example.PetApp.repository.mongo.ChatMessageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static com.example.PetApp.domain.ChatMessage.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatRoomServiceImp implements ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;
    private final ProfileRepository profileRepository;
    private final StringRedisTemplate redisTemplate;
    private final ChatMessageRepository chatMessageRepository;
    private final ChattingReader chattingReader;


    @Transactional
    @Override
    public ResponseEntity<?> getChatRooms(Long profileId) {
        Optional<Profile> profile = profileRepository.findById(profileId);
        if (profile.isEmpty()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("권한이 없습니다.");
        }
        Set<ChatRoom> chatRoomList = chatRoomRepository.findAllByProfilesContains(profile.get());
        List<ChatRoomsResponseDto> chatRoomsResponseDtos = chatRoomList.stream().map(chatRoom -> {
            String lastMessage = redisTemplate.opsForValue().get("chat:lastMessage" + chatRoom.getChatRoomId());
            String lastMessageTime = redisTemplate.opsForValue().get("chat:lastMessageTime" + chatRoom.getChatRoomId());
            String count = redisTemplate.opsForValue().get("unRead:" + chatRoom.getChatRoomId() + ":" + profileId);
            int unReadCount = count != null ? Integer.parseInt(count) : 0;
            LocalDateTime lastMessageLocalDateTime = null;
            if (lastMessageTime != null) {
                lastMessageLocalDateTime = LocalDateTime.parse(lastMessageTime);
            }
            return ChatRoomsResponseDto.from(chatRoom, lastMessage, unReadCount, lastMessageLocalDateTime);
        }).collect(Collectors.toList());
        return ResponseEntity.ok(chatRoomsResponseDtos);
    }

    @Transactional
    @Override
    public ResponseEntity<?> createChatRoom(WalkingTogetherPost walkingTogetherPost, Profile profile) {
        Optional<ChatRoom> chatRoom2 = chatRoomRepository.findByWalkingTogetherPost(walkingTogetherPost);
        if (chatRoom2.isEmpty()) {//채팅방이 없으면 새로운생성 있으면 profiles에 신청자 Profile 추가
            ChatRoom chatRoom = ChatRoom.builder()
                    .name(walkingTogetherPost.getProfile().getPetName()+"님의 방")
                    .limitCount(walkingTogetherPost.getLimitCount())//나중에 게시물에서 인원 수를 고정.
                    .walkingTogetherPost(walkingTogetherPost)
                    //이게 수정에서 가능하려나?
                    .build();
            chatRoom.addProfiles(walkingTogetherPost.getProfile());//글 작성자.
            chatRoom.addProfiles(profile);//신청하는사람.
            ChatRoom chatRoom1 = chatRoomRepository.save(chatRoom);
            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("chatRoomId",chatRoom1.getChatRoomId()));
        } else {
            if (walkingTogetherPost.getLimitCount() <= chatRoom2.get().getProfiles().size()) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body("인원초과");//채팅방 limitCount설정.
            }
            ChatRoom chatRoom = chatRoom2.get();
            chatRoom.addProfiles(profile);
            return ResponseEntity.ok().build();
        }
    }

    @Override
    @Transactional
    public ResponseEntity<?> deleteChatRoom(Long chatRoomId, Long profileId) {
        Optional<ChatRoom> chatRoom = chatRoomRepository.findById(chatRoomId);
        Optional<Profile> profile = profileRepository.findById(profileId);
        ChatRoom chatRoom1 = chatRoom.get();
        List<Profile> profiles = chatRoom1.getProfiles();
        profiles.remove(profile.get());
        chatRoom1.setProfiles(profiles);//방 사용자 수가 1이되면 채팅방 전체 삭제.
        if (chatRoomRepository.countByProfile(chatRoomId) == 1) {
            profiles.clear();
            chatRoom1.setProfiles(profiles);//여기까지되는데
            chatMessageRepository.deleteByChatRoomId(chatRoomId);//채팅방 삭제.
            chatRoomRepository.deleteByChatRoom(chatRoomId);//이게 왜안되는교?
        }
        return ResponseEntity.ok().body("삭제 되었습니다.");
    }

    @Override
    public List<Long> getProfiles(Long chatRoomId) {
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId).orElseThrow(() -> new RuntimeException("채팅방 없습니다."));

        return chatRoom
                .getProfiles()
                .stream()
                .map(Profile::getProfileId)
                .collect(Collectors.toList());
    }

    @Transactional
    @Override//방장만 수정할 수 있도록 설정.
    public ResponseEntity<?> updateChatRoom(Long chatRoomId, UpdateChatRoomDto updateChatRoomDto, Long profileId) {
        Optional<ChatRoom> chatRoom = chatRoomRepository.findById(chatRoomId);
        Optional<Profile> profile = profileRepository.findById(profileId);
        if (chatRoom.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("해당 채팅방이 없습니다.");
        }
        if (profile.isEmpty()||!(chatRoom.get().getWalkingTogetherPost().getProfile().getProfileId().equals(profile.get().getProfileId()))) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("수정 권한이 없습니다.");
        }
        ChatRoom chatRoom1 = chatRoom.get();
        chatRoom1.setName(updateChatRoomDto.getChatRoomName());
        chatRoom1.setLimitCount(updateChatRoomDto.getLimitCount());
        return ResponseEntity.ok().body("수정 되었습니다.");
    }

    @Transactional
    @Override
    public ResponseEntity<?> getMessages(Long chatRoomId, Long userId, int page) {
        return chattingReader.getMessages(chatRoomId, userId, ChatRoomType.MANY, page);
    }

}
