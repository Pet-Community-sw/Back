package com.example.PetApp.service.comment;

import com.example.PetApp.dto.commment.CommentDto;
import com.example.PetApp.dto.commment.UpdateCommentDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public interface CommentService {
    ResponseEntity<?> createComment(CommentDto commentDto, String email) throws JsonProcessingException;

    ResponseEntity<String> deleteComment(Long commentId, String email);

    ResponseEntity<String> updateComment(Long commentId, UpdateCommentDto updateCommentDto, String  email);
}
