package com.example.PetApp.service.walkerpost;

import com.example.PetApp.dto.walkerpost.CreateWalkerPostDto;
import com.example.PetApp.dto.walkerpost.UpdateWalkerPostDto;
import com.example.PetApp.repository.jpa.WalkerPostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;


@RequiredArgsConstructor
@Service
public class WalkerPostServiceImp implements WalkerPostService{

    private WalkerPostRepository walkerPostRepository;

    @Override
    public ResponseEntity<?> createWalkerPost(CreateWalkerPostDto createWalkerPostDto, String email) {
        return null;
    }

    @Override
    public ResponseEntity<?> applyToWalkerPost(Long walkerPostId, String email) {
        return null;
    }

    @Override
    public ResponseEntity<?> getWalkerPostsByLocation(Double minLongitude, Double minLatitude, Double maxLongitude, Double maxLatitude, String email) {
        return null;
    }

    @Override
    public ResponseEntity<?> getWalkerPostsByPlace(Double longitude, Double latitude, String email) {
        return null;
    }

    @Override
    public ResponseEntity<?> getWalkerPost(Long walkerPostId, String email) {
        return null;
    }

    @Override
    public ResponseEntity<?> updateWalkerPost(Long walkerPostId, UpdateWalkerPostDto updateWalkerPostDto, String email) {
        return null;
    }

    @Override
    public ResponseEntity<?> deleteWalkerPost(Long walkerPostId, String email) {
        return null;
    }

    @Override
    public ResponseEntity<?> getApplicants(Long walkerPostId, String email) {
        return null;
    }
}
