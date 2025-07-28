package com.example.PetApp.service.like;

import com.example.PetApp.dto.like.LikeResponseDto;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public interface LikeService {

    ResponseEntity<?> createAndDeleteLike(Long postId, String email);

    LikeResponseDto getLikes(Long postId);
}
