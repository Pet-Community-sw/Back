package com.example.PetApp.service.chat;

import com.example.PetApp.domain.*;
import com.example.PetApp.dto.groupchat.ChatMessageDto;
import com.example.PetApp.dto.groupchat.ChatMessageResponseDto;
import com.example.PetApp.dto.groupchat.UpdateChatUnReadCountDto;
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

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChattingReaderImp implements ChattingReader{

    private final ChatRoomRepository chatRoomRepository;
    private final ProfileRepository profileRepository;
    private final MemberRepository memberRepository;
    private final MemberChatRoomRepository memberChatRoomRepository;
    private final SimpMessagingTemplate simpMessagingTemplate;
    private final StringRedisTemplate redisTemplate;
    private final ChatMessageRepository chatMessageRepository;


    @Transactional
    @Override
    public ResponseEntity<?> getMessages(Long chatRoomId, Long userId, ChatMessage.ChatRoomType chatRoomType, int page) {
        log.info("getMessages 요청 chatRoomId : {}, userId :{}, chatRoomType : {}", chatRoomId, userId, chatRoomType);
        Pageable pageRequest = PageRequest.of(page, 20, Sort.by(Sort.Direction.DESC, "messageTime"));
        String key = null;
        if (chatRoomType == ChatMessage.ChatRoomType.MANY) {
            Optional<ChatRoom> chatRoom = chatRoomRepository.findById(chatRoomId);
            Optional<Profile> profile = profileRepository.findById(userId);
            if (chatRoom.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("해당 채팅방이 없습니다.");
            } else if (profile.isEmpty() || !(chatRoom.get().getProfiles().contains(profile.get()))) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("권한이 없습니다.");
            }
            key = "unReadChatCount:" + chatRoomId + ":" + userId;
        } else if (chatRoomType == ChatMessage.ChatRoomType.ONE) {
            Optional<MemberChatRoom> memberChatRoom = memberChatRoomRepository.findById(chatRoomId);
            Optional<Member> member = memberRepository.findById(userId);
            if (memberChatRoom.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("해당 채팅방이 없습니다.");
            } else if (member.isEmpty() || !(memberChatRoom.get().getMembers().contains(member.get()))) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("권한이 없습니다.");
            }
            key = "unReadMemberChatCount:" + chatRoomId + ":" + userId;
        }

        redisTemplate.delete(key);
        Page<ChatMessage> messages = chatMessageRepository.findAllByChatRoomIdAndChatRoomType(chatRoomId, chatRoomType, pageRequest);

        List<ChatMessage> content = messages.getContent();

        for (ChatMessage chatMessage : content) {
            updateChatMessageProfile(chatMessage, userId);
        }
        List<ChatMessageDto> chatMessageDtos = content.stream()
                .map(chatMessage -> new ChatMessageDto(
                        chatMessage.getSenderId(),
                        chatMessage.getSenderName(),
                        chatMessage.getSenderImageUrl(),
                        chatMessage.getMessage(),
                        chatMessage.getChatUnReadCount(),
                        chatMessage.getMessageTime()
                ))
                .collect(Collectors.toList());
        ChatMessageResponseDto messagesList = new ChatMessageResponseDto(chatRoomId, chatMessageDtos);
        return ResponseEntity.ok(messagesList);
    }

    public void updateChatMessageProfile(ChatMessage chatMessage, Long currentUserId) {
        List<Long> offlineUsers = chatMessage.getUsers();

        // 자신을 제외한 리스트로 새로 만듦
        List<Long> updatedOfflineProfiles = offlineUsers.stream()
                .filter(id -> !id.equals(currentUserId))
                .collect(Collectors.toList());

        // 업데이트된 리스트 세팅
        chatMessage.setUsers(updatedOfflineProfiles);

        chatMessage.setChatUnReadCount(chatMessage.getUsers().size());

        chatMessageRepository.save(chatMessage);//카톡처럼 많은 트래픽이 발생안할것같아 이렇게함.

        UpdateChatUnReadCountDto updateChatUnReadDto=UpdateChatUnReadCountDto.builder()
                .chatRoomId(chatMessage.getChatRoomId())
                .id(chatMessage.getId())
                .chatUnReadCount(chatMessage.getChatUnReadCount())
                .build();
        simpMessagingTemplate.convertAndSend("/sub/chat/update/unReadCount", updateChatUnReadDto);//깃에 작성해야됨.
        //이거 api명세서 작성해야됨. 안읽은 수 처리.
    }
}
