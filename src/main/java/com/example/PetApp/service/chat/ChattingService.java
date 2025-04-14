package com.example.PetApp.service.chat;

import com.example.PetApp.config.redis.RedisPublish;
import com.example.PetApp.domain.ChatMessage;
import com.example.PetApp.domain.Profile;
import com.example.PetApp.repository.jpa.ProfileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;


@Service
@RequiredArgsConstructor
@Slf4j//코드 리펙토링 필수
public class ChattingService {
    private final RedisPublish redisPublish;
    private final ProfileRepository profileRepository;
    private final StringRedisTemplate stringRedisTemplate;
    private final ChatRoomService chatRoomService;


    public void sendToMessage(ChatMessage chatMessage, Long profileId) {
        if (!(chatMessage.getSenderId().equals(profileId))) {
            throw new IllegalArgumentException("사용자가 동일하지 않습니다.");
        }
        Profile profile = profileRepository.findById(profileId).get();
        log.info("messageType : {}", chatMessage);

        if (chatMessage.getMessageType() == ChatMessage.MessageType.ENTER) {
            chatMessage.setMessage(profile.getDogName() + "님이 입장하셨습니다.");
            chatMessage.setMessageTime(LocalDateTime.now());
            redisPublish.publish(chatMessage);

        } else if (chatMessage.getMessageType() == ChatMessage.MessageType.LEAVE) {
            chatMessage.setMessage(profile.getDogName() + "님이 나가셨습니다.");
            stringRedisTemplate.delete("chat:lastMessage" + chatMessage.getChatRoomId());//해당 redis 삭제.
            stringRedisTemplate.delete("chat:lastMessageTime" + chatMessage.getChatRoomId());
            stringRedisTemplate.delete("unRead:" + chatMessage.getChatRoomId() + ":" + profile.getProfileId());
            chatMessage.setMessageTime(LocalDateTime.now());//세션 제거를 해야하는데 어떻게해 ?
            redisPublish.publish(chatMessage);
            chatRoomService.deleteChatRoom(chatMessage.getChatRoomId(), profileId);//leave하는 순간 채팅방 나가게
        }else {
            chatMessage.setMessageTime(LocalDateTime.now());
            redisPublish.publish(chatMessage);
        }
    }

}
