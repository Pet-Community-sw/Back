package com.example.PetApp.service.chat;

import com.example.PetApp.dto.chat.CreateChatRoomDto;
import com.example.PetApp.dto.chat.UpdateChatRoomDto;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public interface ChatRoomService {

    ResponseEntity<?> getChatRoomList(Long profileId, String email);

    ResponseEntity<?> createChatRoom(CreateChatRoomDto createChatRoomDto, Long profileId, String email);

    ResponseEntity<?> deleteChatRoom(Long chatRoomId, Long profileId, String email);

    ResponseEntity<?> updateChatRoom(UpdateChatRoomDto updateChatRoomDto, Long profileId, String email);


    ResponseEntity<?> getMessages(Long chatRoomId, Long profileId, String email, int page);
}
