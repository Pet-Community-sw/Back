package com.example.PetApp.service.walkerpost;

import com.example.PetApp.dto.delegateWalkpost.CreateDelegateWalkPostDto;
import com.example.PetApp.dto.delegateWalkpost.UpdateDelegateWalkPostDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public interface DelegateWalkPostService {
    ResponseEntity<?> createDelegateWalkPost(CreateDelegateWalkPostDto createDelegateWalkPostDto, Long profileId);

    ResponseEntity<?> applyToDelegateWalkPost(Long delegateWalkPostId, String content, String email) throws JsonProcessingException;

    ResponseEntity<?> getDelegateWalkPostsByLocation(Double minLongitude, Double minLatitude, Double maxLongitude, Double maxLatitude, String email);

    ResponseEntity<?> getDelegateWalkPostsByPlace(Double longitude, Double latitude, String email);

    ResponseEntity<?> getDelegateWalkPost(Long delegateWalkPostId, String email);

    ResponseEntity<?> updateDelegateWalkPost(Long delegateWalkPostId, UpdateDelegateWalkPostDto updateDelegateWalkPostDto, String email);

    ResponseEntity<?> deleteDelegateWalkPost(Long delegateWalkPostId, String email);

    ResponseEntity<?> getApplicants(Long delegateWalkPostId, Long profileId);

    ResponseEntity<?> checkProfile(Long profileId);

    ResponseEntity<?> selectApplicant(Long delegateWalkPostId, Long memberId, String email) throws JsonProcessingException;

    ResponseEntity<?> updateDelegateWalkPost(Long delegateWalkPostId, Long profileId);
}
