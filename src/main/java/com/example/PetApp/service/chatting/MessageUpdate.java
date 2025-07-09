package com.example.PetApp.service.chatting;

import com.example.PetApp.domain.ChatMessage;
import com.example.PetApp.dto.groupchat.UpdateChatUnReadCountDto;
import com.example.PetApp.mapper.ChatRoomMapper;
import com.example.PetApp.repository.mongo.ChatMessageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class MessageUpdate {
    private final SimpMessagingTemplate simpMessagingTemplate;
    private final ChatMessageRepository chatMessageRepository;


    public void updateProfilesForMessages(List<ChatMessage> messages, Long userId) {
        for (ChatMessage chatMessage : messages) {
            updateChatMessageProfile(chatMessage, userId);
        }
    }

    public void updateChatMessageProfile(ChatMessage chatMessage, Long currentUserId) {
        List<Long> offlineUsers = chatMessage.getUsers();

        // 자신을 제외한 리스트로 새로 만듦
        List<Long> updatedOfflineProfiles = offlineUsers.stream()
                .filter(id -> !id.equals(currentUserId))
                .collect(Collectors.toList());

        // 업데이트된 리스트 세팅
        chatMessage.setUsers(updatedOfflineProfiles);

        chatMessage.setChatUnReadCount(chatMessage.getUsers().size());

        chatMessageRepository.save(chatMessage);//카톡처럼 많은 트래픽이 발생안할것같아 이렇게함.

        UpdateChatUnReadCountDto updateChatUnReadCountDto = ChatRoomMapper.toUpdateChatUnReadCountDto(chatMessage);

        simpMessagingTemplate.convertAndSend("/sub/chat/update/unReadCount", updateChatUnReadCountDto);
        //이거 api명세서 작성해야됨. 안읽은 수 처리.
    }
}
