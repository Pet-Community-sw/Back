package com.example.PetApp.service.walkingtogetherpost;

import com.example.PetApp.dto.walkingtogetherpost.CreateWalkingTogetherPostDto;
import com.example.PetApp.dto.walkingtogetherpost.UpdateWalkingTogetherPostDto;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public interface WalkingTogetherPostService {


    ResponseEntity<?> createWalkingTogetherPost(CreateWalkingTogetherPostDto createWalkingTogetherPostDto, Long profileId);

    ResponseEntity<?> deleteWalkingTogetherPost(Long walkingTogetherPostId, Long profileId);

    ResponseEntity<?> getWalkingTogetherPost(Long walkingTogetherPostId, Long profileId);

    ResponseEntity<?> updateWalkingTogetherPost(Long walkingTogetherPostId, UpdateWalkingTogetherPostDto updateWalkingTogetherPostDto, Long profileId);


    ResponseEntity<?> startMatch(Long walkingTogetherPostId, Long profileId);

    ResponseEntity<?> getWalkingTogetherPostsList(Long recommendRoutePostId, Long profileId);
}
