package com.example.PetApp.service.walkingtogetherpost;

import com.example.PetApp.dto.chatroom.CreateChatRoomResponseDto;
import com.example.PetApp.dto.walkingtogetherpost.CreateWalkingTogetherPostDto;
import com.example.PetApp.dto.walkingtogetherpost.GetWalkingTogetherPostResponseDto;
import com.example.PetApp.dto.walkingtogetherpost.UpdateWalkingTogetherPostDto;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface WalkingTogetherPostService {


    ResponseEntity<?> createWalkingTogetherPost(CreateWalkingTogetherPostDto createWalkingTogetherPostDto, Long profileId);

    void deleteWalkingTogetherPost(Long walkingTogetherPostId, Long profileId);

    ResponseEntity<?> getWalkingTogetherPost(Long walkingTogetherPostId, Long profileId);

    void updateWalkingTogetherPost(Long walkingTogetherPostId, UpdateWalkingTogetherPostDto updateWalkingTogetherPostDto, Long profileId);

    CreateChatRoomResponseDto startMatch(Long walkingTogetherPostId, Long profileId);

    List<GetWalkingTogetherPostResponseDto> getWalkingTogetherPosts(Long recommendRoutePostId, Long profileId);
}
