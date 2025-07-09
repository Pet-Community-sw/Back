package com.example.PetApp.service.chatting;

import com.example.PetApp.dto.groupchat.ChatMessageResponseDto;
import org.springframework.stereotype.Service;

import static com.example.PetApp.domain.ChatMessage.*;

@Service
public interface ChattingReader  {
    ChatMessageResponseDto getMessages(Long chatRoomId, Long userId, ChatRoomType chatRoomType, int page);

}
