package com.example.PetApp.service.like;

import com.example.PetApp.dto.like.LikeDto;
import com.example.PetApp.dto.like.LikeResponseDto;
import com.example.PetApp.service.comment.PostType;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public interface LikeService {

    ResponseEntity<?> createAndDeleteLike(LikeDto likeDto, String email);

    LikeResponseDto getLikes(Long postId);
}
