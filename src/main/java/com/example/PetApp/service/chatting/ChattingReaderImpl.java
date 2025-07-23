package com.example.PetApp.service.chatting;

import com.example.PetApp.domain.*;
import com.example.PetApp.dto.groupchat.ChatMessageDto;
import com.example.PetApp.dto.groupchat.ChatMessageResponseDto;
import com.example.PetApp.mapper.ChatRoomMapper;
import com.example.PetApp.repository.mongo.ChatMessageRepository;
import com.example.PetApp.service.chatting.handler.ChatRoomHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChattingReaderImpl implements ChattingReader{

    private final ChatRedisCleaner chatRedisCleaner;
    private final MessageUpdate messageUpdate;
    private final ChatMessageRepository chatMessageRepository;
    private final ChatRoomHandler chatRoomHandler;


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
                chatRoomHandler.verifyChatRoomAccess(chatRoomId, userId);
            }
            case ONE -> {
                chatRoomHandler.verifyMemberChatRoomAccess(chatRoomId, userId);
            }
            default -> {
                throw new IllegalArgumentException("지원하지 않는 채팅방  타입입니다.");
            }
        }
    }
}
