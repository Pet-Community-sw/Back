package com.example.PetApp.service.chatting;

import com.example.PetApp.domain.*;
import com.example.PetApp.dto.groupchat.ChatMessageDto;
import com.example.PetApp.dto.groupchat.ChatMessageResponseDto;
import com.example.PetApp.dto.groupchat.UpdateChatUnReadCountDto;
import com.example.PetApp.exception.ForbiddenException;
import com.example.PetApp.exception.NotFoundException;
import com.example.PetApp.mapper.ChatRoomMapper;
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
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChattingReaderImp implements ChattingReader{

    private final ChatRoomRepository chatRoomRepository;
    private final ProfileRepository profileRepository;
    private final MemberRepository memberRepository;
    private final MemberChatRoomRepository memberChatRoomRepository;
    private final ChatRedisCleaner chatRedisCleaner;
    private final MessageUpdate messageUpdate;
    private final ChatMessageRepository chatMessageRepository;


    @Transactional
    @Override
    public ChatMessageResponseDto getMessages(Long chatRoomId, Long userId, ChatMessage.ChatRoomType chatRoomType, int page) {
        log.info("getMessages 요청 chatRoomId : {}, userId :{}, chatRoomType : {}", chatRoomId, userId, chatRoomType);

        validateUserInChatRoom(chatRoomId, userId, chatRoomType);

        chatRedisCleaner.redisDeleteUnreadKey(chatRoomId, userId, chatRoomType);

        Pageable pageRequest = PageRequest.of(page, 20, Sort.by(Sort.Direction.DESC, "messageTime"));
        Page<ChatMessage> messages = chatMessageRepository.findAllByChatRoomIdAndChatRoomType(chatRoomId, chatRoomType, pageRequest);

        messageUpdate.updateProfilesForMessages(messages.getContent(), userId);

        List<ChatMessageDto> chatMessageDtos = ChatRoomMapper.toChatMessageDtos(messages.getContent());
        return new ChatMessageResponseDto(chatRoomId, chatMessageDtos);
    }


    private void validateUserInChatRoom(Long chatRoomId, Long userId, ChatMessage.ChatRoomType chatRoomType) {

        switch (chatRoomType) {
            case MANY -> {
                ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId)
                        .orElseThrow(() -> new NotFoundException("해당 채팅방이 없습니다."));
                Profile profile = profileRepository.findById(userId)
                        .orElseThrow(() -> new ForbiddenException("프로필 설정 해주세요."));
                if (!chatRoom.getProfiles().contains(profile)) {
                    throw new ForbiddenException("권한이 없습니다.");
                }
            }
            case ONE -> {
                MemberChatRoom memberChatRoom = memberChatRoomRepository.findById(chatRoomId)
                        .orElseThrow(() -> new NotFoundException("해당 채팅방이 없습니다."));
                Member member = memberRepository.findById(userId)
                        .orElseThrow(() -> new NotFoundException("해당 회원이 없습니다."));
                if (!memberChatRoom.getMembers().contains(member)) {
                    throw new ForbiddenException("권한이 없습니다.");
                }
            }
            default -> {
                throw new IllegalArgumentException("지원하지 않는 채팅방  타입입니다.");
            }
        }
    }
}
