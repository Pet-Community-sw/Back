package com.example.PetApp.service.chat;

import com.example.PetApp.config.redis.NotificationRedisPublisher;
import com.example.PetApp.config.redis.RedisPublisher;
import com.example.PetApp.domain.ChatMessage;
import com.example.PetApp.domain.Member;
import com.example.PetApp.domain.MemberChatRoom;
import com.example.PetApp.repository.jpa.MemberChatRoomRepository;
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
    private final MemberChatRoomRepository memberChatRoomRepository;

    public void sendToMessage(ChatMessage chatMessage, Long id) {
        if (!(chatMessage.getSenderId().equals(id))) {
            throw new IllegalArgumentException("사용자가 동일하지 않습니다.");
        }
        log.info("messageType : {}", chatMessage);

        chatMessage.setMessageTime(LocalDateTime.now());

        if (chatMessage.getMessageType() == ChatMessage.MessageType.ENTER) {
            chatMessage.setMessage(chatMessage.getSenderName() + "님이 입장하셨습니다.");
            redisPublish.publish(chatMessage);

        } else if (chatMessage.getMessageType() == ChatMessage.MessageType.LEAVE) {
            chatMessage.setMessage(chatMessage.getSenderName() + "님이 나가셨습니다.");
            redisPublish.publish(chatMessage);
            if (chatMessage.getChatRoomType()== ChatMessage.ChatRoomType.MANY) {
                cleanChatRedis(chatMessage, id);
                chatRoomService.deleteChatRoom(chatMessage.getChatRoomId(), id);//leave하는 순간 채팅방 나가게
            }else {
                cleanMemberChatRedis(chatMessage, id);
                memberChatRoomRepository.deleteById(chatMessage.getChatRoomId());
            }
        }else {
            redisPublish.publish(chatMessage);
            sendChatNotification(chatMessage);
        }
    }

    private void sendChatNotification(ChatMessage chatMessage) {//다중 or 1:1
        Long chatRoomId = chatMessage.getChatRoomId();
        Long senderId = chatMessage.getSenderId();
        if (chatMessage.getChatRoomType() == ChatMessage.ChatRoomType.MANY) {
            List<Long> profiles = chatRoomService.getProfiles(chatRoomId);

            for (Long profileId : profiles) {
                if (!profileId.equals(senderId)) {
                    String message = chatMessage.getSenderName() + "님이 메시지를 보냈습니다.";
                    notificationRedisPublisher.publish("member:" + profileId, message);
                }
            }
        } else {
            MemberChatRoom memberChatRoom = memberChatRoomRepository.findById(chatRoomId).orElseThrow(()->new RuntimeException("채팅방 없음."));
            List<Member> members = memberChatRoom.getMembers();
            for (Member member : members) {
                if (!member.getMemberId().equals(senderId)) {
                    String message = chatMessage.getSenderName() + "님이 메시지를 보냈습니다.";
                    notificationRedisPublisher.publish("member:"+member.getMemberId(), message);
                }
            }
        }
    }

    private void cleanChatRedis(ChatMessage chatMessage, Long Id) {//단톡방인지 1:1인지
        stringRedisTemplate.delete("chat:lastMessage" + chatMessage.getChatRoomId());//해당 redis 삭제.
        stringRedisTemplate.delete("chat:lastMessageTime" + chatMessage.getChatRoomId());
        stringRedisTemplate.delete("unReadChat:" + chatMessage.getChatRoomId() + ":" +Id);
    }

    private void cleanMemberChatRedis(ChatMessage chatMessage, Long Id) {//단톡방인지 1:1인지
        stringRedisTemplate.delete("memberChat:lastMessage" + chatMessage.getChatRoomId());//해당 redis 삭제.
        stringRedisTemplate.delete("memberChat:lastMessageTime" + chatMessage.getChatRoomId());
        stringRedisTemplate.delete("unReadMemberChat:" + chatMessage.getChatRoomId() + ":" +Id);
    }

}
