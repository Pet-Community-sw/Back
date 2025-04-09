package com.example.PetApp.service;

import com.example.PetApp.dto.ChatRoomResponseDto;
import com.example.PetApp.dto.CreateChatRoomDto;
import com.example.PetApp.dto.UpdateChatRoomDto;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface ChatRoomService {

    ResponseEntity<?> getChatRoomList(Long profileId, String email);

    ResponseEntity<?> createChatRoom(CreateChatRoomDto createChatRoomDto, String email);

    ResponseEntity<?> deleteChatRoom(Long chatRoomId, Long profileId, String email);

    ResponseEntity<?> updateChatRoom(UpdateChatRoomDto updateChatRoomDto, String email);
}
