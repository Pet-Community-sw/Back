package com.example.PetApp.service;

import com.example.PetApp.dto.commment.CreateCommentDto;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public interface CommentService {
    ResponseEntity<?> createComment(CreateCommentDto createCommentDto);

//    ResponseEntity<?> updateComment(Long commentId, String email);
}
