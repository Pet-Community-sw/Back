package com.example.PetApp.service.chat;

import com.example.PetApp.config.redis.RedisPublisher;
import com.example.PetApp.domain.*;
import com.example.PetApp.dto.groupchat.ChatMessageDto;
import com.example.PetApp.dto.groupchat.ChatMessageResponseDto;
import com.example.PetApp.dto.groupchat.UpdateChatUnReadCountDto;
import com.example.PetApp.repository.jpa.ChatRoomRepository;
import com.example.PetApp.repository.jpa.MemberChatRoomRepository;
import com.example.PetApp.repository.jpa.MemberRepository;
import com.example.PetApp.repository.jpa.ProfileRepository;
import com.example.PetApp.repository.mongo.ChatMessageRepository;
import com.example.PetApp.util.SendNotificationUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
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
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChattingService {
    private final RedisPublisher redisPublish;
    private final ProfileRepository profileRepository;
    private final StringRedisTemplate stringRedisTemplate;
    private final ChatMessageRepository chatMessageRepository;
    private final ChatRoomService chatRoomService;
    private final MemberChatRoomRepository memberChatRoomRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final MemberRepository memberRepository;
    private final SendNotificationUtil sendNotificationUtil;
    private final SimpMessagingTemplate simpMessagingTemplate;
    private final StringRedisTemplate redisTemplate;


    public void sendToMessage(ChatMessage chatMessage, Long id) throws JsonProcessingException {
        if (!chatMessage.getSenderId().equals(id)) {
            throw new IllegalArgumentException("사용자가 동일하지 않습니다.");
        }

        log.info("메시지 처리 시작 - chatRoomType: {}, messageType: {}", chatMessage.getChatRoomType(), chatMessage.getMessageType());

        if (chatMessage.getChatRoomType() == ChatMessage.ChatRoomType.MANY) {
            Profile profile = profileRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("프로필을 찾을 수 없습니다."));
            log.info("프로필 조회 완료: {}", profile.getPetName());

            ChatRoom chatRoom = chatRoomRepository.findById(chatMessage.getChatRoomId())
                    .orElseThrow(() -> new RuntimeException("채팅방 없음"));
            log.info("채팅방 조회 완료 (MANY): {}", chatRoom.getChatRoomId());

            chatMessage.setSenderImageUrl(profile.getPetImageUrl());
            chatMessage.setSenderName(profile.getPetName());
            setOfflineProfilesAndUnreadCount(chatMessage, chatRoom);

        } else if (chatMessage.getChatRoomType() == ChatMessage.ChatRoomType.ONE) {
            Member member = memberRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("회원을 찾을 수 없습니다."));
            log.info("멤버 조회 완료: {}", member.getName());

            MemberChatRoom memberChatRoom = memberChatRoomRepository.findById(chatMessage.getChatRoomId())
                    .orElseThrow(() -> new RuntimeException("채팅방 없음."));
            log.info("채팅방 조회 완료 (ONE): {}", memberChatRoom.getMemberChatRoomId());

            chatMessage.setSenderImageUrl(member.getMemberImageUrl());
            chatMessage.setSenderName(member.getName());
            setOfflineMembersAndUnreadCount(chatMessage, memberChatRoom);

        } else {
            throw new IllegalArgumentException("잘못된 chatType");
        }

        chatMessage.setMessageTime(LocalDateTime.now());

        if (chatMessage.getMessageType() == ChatMessage.MessageType.ENTER) {
            chatMessage.setMessage(chatMessage.getSenderName() + "님이 입장하셨습니다.");
            log.info("입장 메시지: {}", chatMessage.getMessage());
            redisPublish.publish(chatMessage);

        } else if (chatMessage.getMessageType() == ChatMessage.MessageType.LEAVE) {
            chatMessage.setMessage(chatMessage.getSenderName() + "님이 나가셨습니다.");
            log.info("퇴장 메시지: {}", chatMessage.getMessage());
            redisPublish.publish(chatMessage);

            if (chatMessage.getChatRoomType() == ChatMessage.ChatRoomType.MANY) {
                log.info("채팅방 퇴장 처리 (MANY): chatRoomId={}, senderId={}", chatMessage.getChatRoomId(), id);
                cleanChatRedis(chatMessage, id);
                chatRoomService.deleteChatRoom(chatMessage.getChatRoomId(), id);
            } else {
                log.info("채팅방 퇴장 처리 (ONE): chatRoomId={}, senderId={}", chatMessage.getChatRoomId(), id);
                cleanMemberChatRedis(chatMessage, id);
                memberChatRoomRepository.deleteById(chatMessage.getChatRoomId());
            }

        } else {
            log.info("일반 메시지 발행");
            redisPublish.publish(chatMessage);
            sendChatNotification(chatMessage);
        }

        log.info("메시지 처리 완료");
    }

    private void setOfflineProfilesAndUnreadCount(ChatMessage chatMessage, ChatRoom chatRoom) {
        List<Profile> profiles = chatRoom.getProfiles();
        Set<String> onlineProfiles = stringRedisTemplate.opsForSet()
                .members("chatRoomId:" + chatRoom.getChatRoomId() + ":onlineProfiles");
        List<Long> offlineProfiles = profiles.stream()
                .map(Profile::getProfileId)
                .filter(profileId -> onlineProfiles == null || !onlineProfiles.contains(profileId.toString()))
                .collect(Collectors.toList());

        chatMessage.setUsers(offlineProfiles);
        chatMessage.setChatUnReadCount(offlineProfiles.size());

        log.info("오프라인 프로필 목록 설정 (MANY): {}", offlineProfiles);
    }

    private void setOfflineMembersAndUnreadCount(ChatMessage chatMessage, MemberChatRoom memberChatRoom) {
        List<Member> members = memberChatRoom.getMembers();
        Set<String> onlineMembers = stringRedisTemplate.opsForSet()
                .members("memberChatRoomId:" + memberChatRoom.getMemberChatRoomId() + ":onlineMembers");
        List<Long> offlineMembers = members.stream()
                .map(Member::getMemberId)
                .filter(memberId -> onlineMembers == null || !onlineMembers.contains(memberId.toString()))
                .collect(Collectors.toList());

        chatMessage.setUsers(offlineMembers);
        chatMessage.setChatUnReadCount(offlineMembers.size());

        log.info("오프라인 멤버 목록 설정 (ONE): {}", offlineMembers);
    }

    private void sendChatNotification(ChatMessage chatMessage) throws JsonProcessingException {
        Long chatRoomId = chatMessage.getChatRoomId();
        Long senderId = chatMessage.getSenderId();

        String message = chatMessage.getSenderName() + "님이 메시지를 보냈습니다.";

        if (chatMessage.getChatRoomType() == ChatMessage.ChatRoomType.MANY) {
            List<Long> profiles = chatRoomService.getProfiles(chatRoomId);
            Set<String> onlineProfiles = stringRedisTemplate.opsForSet()
                    .members("chatRoomId:" + chatRoomId + ":onlineProfiles");

            for (Long profileId : profiles) {
                if (!profileId.equals(senderId)) {
                    if (onlineProfiles != null && onlineProfiles.contains(profileId.toString())) continue;

                    Profile profile = profileRepository.findById(profileId).get();
                    log.info("알림 전송 대상 (MANY) - memberId: {}", profile.getMember().getMemberId());
                    sendNotificationUtil.sendNotification(profile.getMember(), message);
                }
            }
        } else {
            MemberChatRoom memberChatRoom = memberChatRoomRepository.findById(chatRoomId)
                    .orElseThrow(() -> new RuntimeException("채팅방 없음."));
            Set<String> onlineMembers = stringRedisTemplate.opsForSet()
                    .members("memberChatRoomId:" + memberChatRoom.getMemberChatRoomId() + ":onlineMembers");
            List<Member> members = memberChatRoom.getMembers();

            for (Member member : members) {
                if (!member.getMemberId().equals(senderId)) {
                    if (onlineMembers != null && onlineMembers.contains(member.getMemberId().toString())) continue;

                    log.info("알림 전송 대상 (ONE) - memberId: {}", member.getMemberId());
                    sendNotificationUtil.sendNotification(member, message);
                }
            }
        }
    }

    private void cleanChatRedis(ChatMessage chatMessage, Long id) {
        stringRedisTemplate.delete("chat:lastMessage" + chatMessage.getChatRoomId());
        stringRedisTemplate.delete("chat:lastMessageTime" + chatMessage.getChatRoomId());
        stringRedisTemplate.delete("unReadChat:" + chatMessage.getChatRoomId() + ":" + id);
        log.info("Redis 정리 완료 (MANY): {}", chatMessage.getChatRoomId());
    }

    private void cleanMemberChatRedis(ChatMessage chatMessage, Long id) {
        stringRedisTemplate.delete("memberChat:lastMessage" + chatMessage.getChatRoomId());
        stringRedisTemplate.delete("memberChat:lastMessageTime" + chatMessage.getChatRoomId());
        stringRedisTemplate.delete("unReadMemberChat:" + chatMessage.getChatRoomId() + ":" + id);
        log.info("Redis 정리 완료 (ONE): {}", chatMessage.getChatRoomId());
    }


    @Transactional
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
