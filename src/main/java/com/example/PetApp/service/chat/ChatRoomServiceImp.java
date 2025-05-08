package com.example.PetApp.service.chat;

import com.example.PetApp.domain.*;
import com.example.PetApp.dto.groupchat.ChatMessageDto;
import com.example.PetApp.dto.groupchat.ChatRoomsResponseDto;
import com.example.PetApp.dto.groupchat.UpdateChatRoomDto;
import com.example.PetApp.repository.jpa.ChatRoomRepository;
import com.example.PetApp.repository.jpa.ProfileRepository;
import com.example.PetApp.repository.mongo.ChatMessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatRoomServiceImp implements ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;
    private final ProfileRepository profileRepository;
    private final StringRedisTemplate redisTemplate;
    private final ChatMessageRepository chatMessageRepository;

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
    @Override//채팅방 생성은 두 가지 일듯 매칭글에서 채팅하기 or 그냥 메시지 보내기
    public ResponseEntity<?> createChatRoom(MatchPost matchPost, Profile profile) {
        Optional<ChatRoom> chatRoom2 = chatRoomRepository.findByMatchPost(matchPost);
        if (chatRoom2.isEmpty()) {//채팅방이 없으면 새로운생성 있으면 profiles에 신청자 Profile 추가
            ChatRoom chatRoom = ChatRoom.builder()
                    .name(matchPost.getProfile().getPetName()+"님의 방")
                    .limitCount(matchPost.getLimitCount())//나중에 게시물에서 인원 수를 고정.
                    .matchPost(matchPost)
                    //이게 수정에서 가능하려나?
                    .build();
            chatRoom.addProfiles(matchPost.getProfile());//글 작성자.
            chatRoom.addProfiles(profile);//신청하는사람.
            ChatRoom chatRoom1 = chatRoomRepository.save(chatRoom);
            return ResponseEntity.status(HttpStatus.CREATED).body(chatRoom1.getChatRoomId());
        } else {
            if (matchPost.getLimitCount() <= chatRoom2.get().getProfiles().size()) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body("인원초과");//채팅방 limitCount설정.
            }
            ChatRoom chatRoom = chatRoom2.get();
            chatRoom.addProfiles(profile);
            return ResponseEntity.ok().build();
        }
    }

    @Transactional
    public void deleteChatRoom(Long chatRoomId, Long profileId) {
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
    public ResponseEntity<?> updateChatRoom(UpdateChatRoomDto updateChatRoomDto, Long profileId) {
        Optional<ChatRoom> chatRoom = chatRoomRepository.findById(updateChatRoomDto.getChatRoomId());
        Optional<Profile> profile = profileRepository.findById(profileId);
        if (chatRoom.isEmpty()) {
            return ResponseEntity.badRequest().body("잘못된 요청입니다.");
        }
        if (profile.isEmpty()||!(chatRoom.get().getMatchPost().getProfile().getProfileId().equals(profile.get().getProfileId()))) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("수정 권한이 없습니다.");
        }
        ChatRoom chatRoom1 = chatRoom.get();
        chatRoom1.setName(updateChatRoomDto.getChatRoomTitle());
        chatRoom1.setLimitCount(updateChatRoomDto.getLimitCount());
        return ResponseEntity.ok().build();
    }

    @Transactional
    @Override//이렇게 하면 채팅방에 적속해 있을 때 unread 증가하는데 이러면 안됨.
    public ResponseEntity<?> getMessages(Long chatRoomId, Long profileId, int page) {
        Optional<ChatRoom> chatRoom = chatRoomRepository.findById(chatRoomId);
        Optional<Profile> profile = profileRepository.findById(profileId);
        if (profile.isEmpty()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("권한이 없습니다.");
        } else if (chatRoom.isEmpty()||!(chatRoom.get().getProfiles().contains(profile.get()))) {
            return ResponseEntity.badRequest().body("잘못된 요청입니다.");
        }
        Pageable pageRequest = PageRequest.of(page, 20, Sort.by(Sort.Direction.DESC, "messageTime"));
        Page<ChatMessage> messages = chatMessageRepository.findByChatRoomId(chatRoomId, pageRequest);
        String key = "unRead:" + chatRoomId + ":" + profileId;
        redisTemplate.delete(key);
        ChatMessageDto messagesList = new ChatMessageDto(chatRoomId, messages.getContent());
        return ResponseEntity.ok(messagesList);
    }

}
