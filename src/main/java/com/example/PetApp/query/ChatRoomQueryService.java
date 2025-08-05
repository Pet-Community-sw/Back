package com.example.PetApp.query;

import com.example.PetApp.domain.ChatRoom;
import com.example.PetApp.exception.NotFoundException;
import com.example.PetApp.repository.jpa.ChatRoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ChatRoomQueryService {

    private final ChatRoomRepository chatRoomRepository;

    public ChatRoom findByChatRoom(Long chatRoomId) {
        return chatRoomRepository.findById(chatRoomId).orElseThrow(() -> new NotFoundException("해당 채팅방은 없습니다."));
    }
}
