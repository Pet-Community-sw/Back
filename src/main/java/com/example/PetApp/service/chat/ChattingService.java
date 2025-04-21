package com.example.PetApp.service.chat;

import com.example.PetApp.config.redis.NotificationRedisPublisher;
import com.example.PetApp.config.redis.RedisPublisher;
import com.example.PetApp.domain.ChatMessage;
import com.example.PetApp.domain.Profile;
import com.example.PetApp.repository.jpa.ProfileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;


@Service
@RequiredArgsConstructor
@Slf4j//코드 리펙토링 필수
public class ChattingService {
    private final RedisPublisher redisPublish;
    private final ProfileRepository profileRepository;
    private final StringRedisTemplate stringRedisTemplate;
    private final ChatRoomService chatRoomService;
    private final NotificationRedisPublisher notificationRedisPublisher;

    public void sendToMessage(ChatMessage chatMessage, Long profileId) {
        if (!(chatMessage.getSenderId().equals(profileId))) {
            throw new IllegalArgumentException("사용자가 동일하지 않습니다.");
        }
        Profile profile = profileRepository.findById(profileId).get();
        log.info("messageType : {}", chatMessage);

        String dogName = profile.getPetName();
        chatMessage.setSenderName(dogName);
        chatMessage.setMessageTime(LocalDateTime.now());

        if (chatMessage.getMessageType() == ChatMessage.MessageType.ENTER) {
            chatMessage.setMessage(dogName + "님이 입장하셨습니다.");
            redisPublish.publish(chatMessage);

        } else if (chatMessage.getMessageType() == ChatMessage.MessageType.LEAVE) {
            chatMessage.setMessage(dogName + "님이 나가셨습니다.");
            cleanRedis(chatMessage, profile);
            redisPublish.publish(chatMessage);
            chatRoomService.deleteChatRoom(chatMessage.getChatRoomId(), profileId);//leave하는 순간 채팅방 나가게
        }else {
            redisPublish.publish(chatMessage);
            sendChatNotification(chatMessage);
        }
    }

    private void sendChatNotification(ChatMessage chatMessage) {
        Long chatRoomId = chatMessage.getChatRoomId();
        Long senderId = chatMessage.getSenderId();
        List<Long> profiles = chatRoomService.getProfiles(chatRoomId);

        for (Long profileId : profiles) {
            if (!profileId.equals(senderId)) {
                String message = chatMessage.getSenderName() + "님이 메시지를 보냈습니다.";
                notificationRedisPublisher.publish("user:"+profileId, message);
            }
        }
    }

    private void cleanRedis(ChatMessage chatMessage, Profile profile) {
        stringRedisTemplate.delete("chat:lastMessage" + chatMessage.getChatRoomId());//해당 redis 삭제.
        stringRedisTemplate.delete("chat:lastMessageTime" + chatMessage.getChatRoomId());
        stringRedisTemplate.delete("unRead:" + chatMessage.getChatRoomId() + ":" + profile.getProfileId());
    }

}
