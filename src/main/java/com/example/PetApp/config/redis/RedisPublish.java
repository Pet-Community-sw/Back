package com.example.PetApp.config.redis;

import com.example.PetApp.domain.ChatMessage;
import com.example.PetApp.repository.ChatMessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RedisPublish {

    private final ChannelTopic channelTopic;

    private final RedisTemplate<String, Object> redisTemplate;

    private final ChatMessageRepository chatMessageRepository;

    public void publish(ChatMessage chatMessage) {
        chatMessageRepository.save(chatMessage);
        redisTemplate.convertAndSend(channelTopic.getTopic(), chatMessage);
    }
}
