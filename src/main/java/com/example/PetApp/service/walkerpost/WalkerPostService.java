package com.example.PetApp.service.walkerpost;

import com.example.PetApp.dto.walkerpost.CreateWalkerPostDto;
import com.example.PetApp.dto.walkerpost.UpdateWalkerPostDto;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public interface WalkerPostService {
    ResponseEntity<?> createWalkerPost(CreateWalkerPostDto createWalkerPostDto, String email);

    ResponseEntity<?> applyToWalkerPost(Long walkerPostId, String email);


    ResponseEntity<?> getWalkerPostsByLocation(Double minLongitude, Double minLatitude, Double maxLongitude, Double maxLatitude, String email);

    ResponseEntity<?> getWalkerPostsByPlace(Double longitude, Double latitude, String email);

    ResponseEntity<?> getWalkerPost(Long walkerPostId, String email);

    ResponseEntity<?> updateWalkerPost(Long walkerPostId, UpdateWalkerPostDto updateWalkerPostDto, String email);

    ResponseEntity<?> deleteWalkerPost(Long walkerPostId, String email);

    ResponseEntity<?> getApplicants(Long walkerPostId, String email);
}
