package com.example.PetApp.service;

import com.example.PetApp.dto.commment.CommentDto;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public interface CommentService {
    ResponseEntity<Object> createComment(CommentDto commentDto, String email);

    ResponseEntity<Object> getComment(Long commentId, String email);

    ResponseEntity<String> deleteComment(Long commentId, String email);

    ResponseEntity<String> updateComment(Long commentId, String content, String email);
}
