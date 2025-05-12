package com.example.PetApp.service.chat;

import com.example.PetApp.domain.WalkingTogetherPost;
import com.example.PetApp.domain.Profile;
import com.example.PetApp.dto.groupchat.UpdateChatRoomDto;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface ChatRoomService {

    ResponseEntity<?> getChatRooms(Long profileId);

    ResponseEntity<?> createChatRoom(WalkingTogetherPost walkingTogetherPost, Profile profile);

    ResponseEntity<?> updateChatRoom(Long chatRoomId, UpdateChatRoomDto updateChatRoomDto, Long profileId);


    ResponseEntity<?> getMessages(Long chatRoomId, Long profileId, int page);

    ResponseEntity<?> deleteChatRoom(Long chatRoomId, Long profileId);

    List<Long> getProfiles(Long chatRoomId);
}
