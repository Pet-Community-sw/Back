package com.example.PetApp.service.chat;

import com.example.PetApp.domain.ChatMessage;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import static com.example.PetApp.domain.ChatMessage.*;

@Service
public interface ChattingReader  {
    ResponseEntity<?> getMessages(Long chatRoomId, Long userId, ChatRoomType chatRoomType, int page);

}
