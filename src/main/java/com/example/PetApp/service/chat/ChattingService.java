package com.example.PetApp.service.chat;

import com.example.PetApp.config.redis.RedisPublish;
import com.example.PetApp.domain.ChatMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChattingService {
    private final RedisPublish redisPublish;


    public void sendMessage(ChatMessage chatMessage, Long profileId) {
        if (!(chatMessage.getSenderId().equals(profileId))) {
            throw new IllegalArgumentException("사용자가 동일하지 않습니다.");
        }
        if (chatMessage.getMessageType() == ChatMessage.MessageType.ENTER) {
            chatMessage.setMessage(chatMessage.getSenderName() + "님이 입장하셨습니다.");
        } else if (chatMessage.getMessageType() == ChatMessage.MessageType.QUIT) {
            chatMessage.setMessage(chatMessage.getSenderName() + "님이 나가셨습니다.");
        }

        redisPublish.publish(chatMessage);

    }

}
