package com.example.PetApp.service.chat;

import com.example.PetApp.config.redis.RedisPublish;
import com.example.PetApp.domain.ChatMessage;
import com.example.PetApp.domain.Profile;
import com.example.PetApp.repository.jpa.ProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ChattingService {
    private final RedisPublish redisPublish;
    private final ProfileRepository profileRepository;


    public void sendMessage(ChatMessage chatMessage, Long profileId) {
        if (!(chatMessage.getSenderId().equals(profileId))) {
            throw new IllegalArgumentException("사용자가 동일하지 않습니다.");
        }
        Profile profile = profileRepository.findById(profileId).get();
        if (chatMessage.getMessageType() == ChatMessage.MessageType.ENTER) {
            chatMessage.setMessage(profile.getDogName() + "님이 입장하셨습니다.");
        } else if (chatMessage.getMessageType() == ChatMessage.MessageType.QUIT) {
            chatMessage.setMessage(profile.getDogName() + "님이 나가셨습니다.");
        }
        redisPublish.publish(chatMessage);

    }

}
