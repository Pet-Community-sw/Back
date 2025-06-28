package com.example.PetApp.service.chat;

import com.example.PetApp.domain.*;
import com.example.PetApp.dto.chatroom.CreateChatRoomResponseDto;
import com.example.PetApp.dto.groupchat.*;
import com.example.PetApp.exception.ConflictException;
import com.example.PetApp.exception.ForbiddenException;
import com.example.PetApp.exception.NotFoundException;
import com.example.PetApp.mapper.ChatRoomMapper;
import com.example.PetApp.repository.jpa.ChatRoomRepository;
import com.example.PetApp.repository.jpa.ProfileRepository;
import com.example.PetApp.repository.mongo.ChatMessageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
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


    @Transactional(readOnly = true)
    @Override
    public List<ChatRoomsResponseDto> getChatRooms(Long profileId) {
        Profile profile = profileRepository.findById(profileId)
                .orElseThrow(() -> new ForbiddenException("권한이 없습니다."));
        List<ChatRoom> chatRoomList = chatRoomRepository.findAllByProfilesContains(profile);
        return chatRoomList.stream()
                .map(chatRoom -> toChatRoomsResponseDtoWithRedis(chatRoom, profile.getProfileId()))
                .collect(Collectors.toList());
    }


    @Transactional
    @Override
    public CreateChatRoomResponseDto createChatRoom(WalkingTogetherPost walkingTogetherPost, Profile profile) {
        Optional<ChatRoom> chatRoom1 = chatRoomRepository.findByWalkingTogetherPost(walkingTogetherPost);
        if (chatRoom1.isEmpty()) {//채팅방이 없으면 새로운생성 있으면 profiles에 신청자 Profile 추가
            ChatRoom chatRoom = ChatRoomMapper.toEntity(walkingTogetherPost, profile);
            ChatRoom savedChatRoom = chatRoomRepository.save(chatRoom);
            return new CreateChatRoomResponseDto(savedChatRoom.getChatRoomId(), true);
        } else {
            if (walkingTogetherPost.getLimitCount() <= chatRoom1.get().getProfiles().size()) {
                throw new ConflictException("인원초과");//채팅방 limitCount설정.
            }
            ChatRoom chatRoom = chatRoom1.get();
            chatRoom.addProfiles(profile);
            return new CreateChatRoomResponseDto(chatRoom.getChatRoomId(), false);
        }
    }

    @Transactional(readOnly = true)
    @Override
    public List<Long> getProfiles(Long chatRoomId) {
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId).orElseThrow(() -> new NotFoundException("채팅방 없습니다."));

        return chatRoom
                .getProfiles()
                .stream()
                .map(Profile::getProfileId)
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public void deleteChatRoom(Long chatRoomId, Long profileId) {
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> new NotFoundException("해당 채팅방은 없습니다."));
        Profile profile = profileRepository.findById(profileId)
                .orElseThrow(() -> new ForbiddenException("프로필 등록해주세요."));
        List<Profile> profiles = chatRoom.getProfiles();
        if (!(profiles.contains(profile))) {
            throw new ForbiddenException("권한이 없습니다.");
        }
        profiles.remove(profile);
        chatRoom.setProfiles(profiles);//방 사용자 수가 1이되면 채팅방 전체 삭제.
        if (chatRoomRepository.countByProfile(chatRoomId) <= 1) {
            chatMessageRepository.deleteByChatRoomId(chatRoomId);//채팅방 메시지 삭제.
            chatRoomRepository.deleteByChatRoom(chatRoomId);//이게 왜안되는교?
        }
    }

    @Transactional
    @Override//방장만 수정할 수 있도록 설정.
    public void updateChatRoom(Long chatRoomId, UpdateChatRoomDto updateChatRoomDto, Long profileId) {
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> new NotFoundException("해당 채팅방은 없습니다."));
        Profile profile = profileRepository.findById(profileId)
                .orElseThrow(() -> new ForbiddenException("프로필 등록해주세요."));
        if (!(chatRoom.getWalkingTogetherPost().getProfile().getProfileId().equals(profile.getProfileId()))) {
            throw new ForbiddenException("수정 권한이 없습니다.");
        }
        chatRoom.setName(updateChatRoomDto.getChatRoomName());
        chatRoom.setLimitCount(updateChatRoomDto.getLimitCount());
    }

    @Transactional(readOnly = true)
    @Override
    public ChatMessageResponseDto getMessages(Long chatRoomId, Long userId, int page) {
        return chattingReader.getMessages(chatRoomId, userId, ChatRoomType.MANY, page);
    }

    private ChatRoomsResponseDto toChatRoomsResponseDtoWithRedis(ChatRoom chatRoom, Long profileId) {
        String lastMessage = redisTemplate.opsForValue().get("chat:lastMessage" + chatRoom.getChatRoomId());
        String lastMessageTime = redisTemplate.opsForValue().get("chat:lastMessageTime" + chatRoom.getChatRoomId());
        String count = redisTemplate.opsForValue().get("unRead:" + chatRoom.getChatRoomId() + ":" + profileId);
        int unReadCount = count != null ? Integer.parseInt(count) : 0;
        LocalDateTime lastMessageLocalDateTime = null;
        if (lastMessageTime != null) {
            lastMessageLocalDateTime = LocalDateTime.parse(lastMessageTime);
        }
        return ChatRoomMapper.toChatRoomsResponseDto(chatRoom, profileId, lastMessage, unReadCount, lastMessageLocalDateTime);
    }
}
