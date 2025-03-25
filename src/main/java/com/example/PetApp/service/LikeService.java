package com.example.PetApp.service;

import com.example.PetApp.dto.like.LikeDto;
import com.example.PetApp.dto.like.LikeResponseDto;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public interface LikeService {

    ResponseEntity<Object> createAndDeleteLike(LikeDto likeDto, String email);

    ResponseEntity<Object> getLike(Long postId);
}
