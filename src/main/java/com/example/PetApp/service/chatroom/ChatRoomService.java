package com.example.PetApp.service.chatroom;

import com.example.PetApp.domain.WalkingTogetherPost;
import com.example.PetApp.domain.Profile;
import com.example.PetApp.dto.chatroom.CreateChatRoomResponseDto;
import com.example.PetApp.dto.groupchat.ChatMessageResponseDto;
import com.example.PetApp.dto.groupchat.ChatRoomsResponseDto;
import com.example.PetApp.dto.groupchat.UpdateChatRoomDto;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface ChatRoomService {

    List<ChatRoomsResponseDto> getChatRooms(Long profileId);

    CreateChatRoomResponseDto createChatRoom(WalkingTogetherPost walkingTogetherPost, Profile profile);

    void updateChatRoom(Long chatRoomId, UpdateChatRoomDto updateChatRoomDto, Long profileId);

    ChatMessageResponseDto getMessages(Long chatRoomId, Long userId, int page);

    void deleteChatRoom(Long chatRoomId, Long profileId);

    List<Long> getProfiles(Long chatRoomId);
}
