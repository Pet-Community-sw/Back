package com.example.PetApp.service;

import com.example.PetApp.dto.commment.CreateCommentDto;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public interface CommentService {
    ResponseEntity<Object> createComment(CreateCommentDto createCommentDto, String email);


    ResponseEntity<Object> getComment(Long commentId, String email);
}
