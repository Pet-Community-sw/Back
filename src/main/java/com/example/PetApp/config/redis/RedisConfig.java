package com.example.PetApp.config.redis;

import com.example.PetApp.domain.ChatMessage;
import lombok.Getter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
@EnableRedisRepositories
@Getter
public class RedisConfig {
    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        return new LettuceConnectionFactory();
    }

    @Bean//채팅을 위한 redisTemplate
    public RedisTemplate<String, Object> redisTemplate() {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory());
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new Jackson2JsonRedisSerializer<>(ChatMessage.class));
        return redisTemplate;
    }

    @Bean//알림을 위한 redisTemplate
    public RedisTemplate<String, Object> notificationRedisTemplate() {
        RedisTemplate<String, Object> notificationRedisTemplate = new RedisTemplate<>();
        notificationRedisTemplate.setConnectionFactory(redisConnectionFactory());
        notificationRedisTemplate.setKeySerializer(new StringRedisSerializer());
        notificationRedisTemplate.setValueSerializer(new Jackson2JsonRedisSerializer<>(Object.class));
        return notificationRedisTemplate;
    }

    @Bean//위치 저장을 위한 redisTemplate
    public RedisTemplate<String, Object> locationRedisTemplate() {
        RedisTemplate<String, Object> locationRedisTemplate = new RedisTemplate<>();
        locationRedisTemplate.setConnectionFactory(redisConnectionFactory());
        locationRedisTemplate.setKeySerializer(new StringRedisSerializer());
        locationRedisTemplate.setHashKeySerializer(new StringRedisSerializer());//해시 설정
        locationRedisTemplate.setValueSerializer(new Jackson2JsonRedisSerializer<>(Object.class));
        locationRedisTemplate.setHashValueSerializer(new Jackson2JsonRedisSerializer<>(Object.class));
        return locationRedisTemplate;
    }
    @Bean
    public ChannelTopic chatRoomChannelTopic() {
        return new ChannelTopic("chatRoom");//나중에 chatRoomId로 바꿔보자.
    }

    @Bean
    public RedisMessageListenerContainer redisMessageListenerContainer(RedisConnectionFactory redisConnectionFactory,
                                                                       MessageListenerAdapter messageListenerAdapter) {
        RedisMessageListenerContainer redisMessageListenerContainer = new RedisMessageListenerContainer();
        redisMessageListenerContainer.setConnectionFactory(redisConnectionFactory);
        redisMessageListenerContainer.addMessageListener(messageListenerAdapter, chatRoomChannelTopic());
        redisMessageListenerContainer.addMessageListener(messageListenerAdapter, memberChatRoomChannelTopic());
        return redisMessageListenerContainer;
    }

    @Bean
    public ChannelTopic memberChatRoomChannelTopic() {
        return new ChannelTopic("memberChatRoom");
    }


    @Bean//topic 설정.
    public RedisMessageListenerContainer notificationRedisMessageListenerContainer(RedisConnectionFactory redisConnectionFactory,
                                                                            NotificationRedisSubscriber notificationRedisSubscriber) {
        RedisMessageListenerContainer notificationRedisMessageListenerContainer = new RedisMessageListenerContainer();
        notificationRedisMessageListenerContainer.setConnectionFactory(redisConnectionFactory);
        notificationRedisMessageListenerContainer.addMessageListener((message, pattern) ->
                    notificationRedisSubscriber.onMessage(
                            new String(message.getChannel()),
                            new String(message.getBody()))
        ,new PatternTopic("member:*")
        );

        return notificationRedisMessageListenerContainer;
    }


    @Bean
    public MessageListenerAdapter messageListenerAdapter(RedisSubscriber redisSubscribe) {
        return new MessageListenerAdapter(redisSubscribe, "sendMessage");
    }
}
