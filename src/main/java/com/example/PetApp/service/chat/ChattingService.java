package com.example.PetApp.service.chat;

import com.example.PetApp.config.redis.RedisPublisher;
import com.example.PetApp.domain.*;
import com.example.PetApp.repository.jpa.*;
import com.example.PetApp.util.SendNotificationUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChattingService {

    private final ChatMessageHandler chatMessageHandler;
    private final ChatHandler chatHandler;

    public void sendToMessage(ChatMessage chatMessage, Long senderId) {
        validateSender(chatMessage, senderId);
        log.info("메시지 처리 시작 - chatRoomType: {}, messageType: {}", chatMessage.getChatRoomType(), chatMessage.getMessageType());

        switch (chatMessage.getChatRoomType()) {
            case MANY -> chatHandler.handleGroupChat(chatMessage, senderId);
            case ONE -> chatHandler.handleOneToOneChat(chatMessage, senderId);
            default -> {
                throw new IllegalArgumentException("지원하지 않는 chatRoomType입니다.");
            }
        }

        chatMessage.setMessageTime(LocalDateTime.now());

        switch (chatMessage.getMessageType()) {
            case ENTER -> chatMessageHandler.handleEnterMessage(chatMessage);
            case LEAVE -> chatMessageHandler.handleLeaveMessage(chatMessage, senderId);
            case TALK -> chatMessageHandler.handleTalkMessage(chatMessage);
            default -> {
                throw new IllegalArgumentException("지원하지 않는 chatMessageType입니다.");
            }

        }

        log.info("메시지 처리 완료");
    }

    private void validateSender(ChatMessage chatMessage, Long senderId) {
        if (!chatMessage.getSenderId().equals(senderId)) {
            throw new IllegalArgumentException("사용자가 동일하지 않습니다.");
        }
    }
}
