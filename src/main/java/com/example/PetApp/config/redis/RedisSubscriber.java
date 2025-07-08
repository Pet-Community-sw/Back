package com.example.PetApp.config.redis;

import com.example.PetApp.config.redis.chathandler.GroupChatHandler;
import com.example.PetApp.config.redis.chathandler.OneToOneChatHandler;
import com.example.PetApp.domain.ChatMessage;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RedisSubscriber {

    private final GroupChatHandler groupChatHandler;
    private final OneToOneChatHandler oneToOneChatHandler;
    private final ObjectMapper objectMapper;

    @Transactional
    public void sendMessage(String message) {
        ChatMessage chatMessage = deserializeMessage(message);
        if (chatMessage.getChatRoomType() == ChatMessage.ChatRoomType.MANY) {
            groupChatHandler.handle(chatMessage);
        } else {
            oneToOneChatHandler.handle(chatMessage);
        }
    }

    private ChatMessage deserializeMessage(String message) {
        try {
            return objectMapper.readValue(message, ChatMessage.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("RedisSubscriber: 메시지 역직렬화 실패", e);
        }
    }
}
