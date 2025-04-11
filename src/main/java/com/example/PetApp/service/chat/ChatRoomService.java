package com.example.PetApp.service.chat;

import com.example.PetApp.dto.chat.CreateChatRoomDto;
import com.example.PetApp.dto.chat.UpdateChatRoomDto;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public interface ChatRoomService {

    ResponseEntity<?> getChatRoomList(Long profileId);

    ResponseEntity<?> createChatRoom(CreateChatRoomDto createChatRoomDto, Long profileId);

    ResponseEntity<?> deleteChatRoom(Long chatRoomId, Long profileId);

    ResponseEntity<?> updateChatRoom(UpdateChatRoomDto updateChatRoomDto, Long profileId);


    ResponseEntity<?> getMessages(Long chatRoomId, Long profileId, int page);
}
