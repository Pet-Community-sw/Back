package com.example.PetApp.service.chat;

import com.example.PetApp.domain.MatchPost;
import com.example.PetApp.domain.Profile;
import com.example.PetApp.dto.groupchat.UpdateChatRoomDto;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface ChatRoomService {

    ResponseEntity<?> getChatRooms(Long profileId);

    ResponseEntity<?> createChatRoom(MatchPost matchPost, Profile profile);

    ResponseEntity<?> updateChatRoom(UpdateChatRoomDto updateChatRoomDto, Long profileId);


    ResponseEntity<?> getMessages(Long chatRoomId, Long profileId, int page);

    void deleteChatRoom(Long chatRoomId, Long profileId);

    List<Long> getProfiles(Long chatRoomId);
}
