package com.example.PetApp.service.comment;

import com.example.PetApp.dto.commment.CommentDto;
import com.example.PetApp.dto.commment.UpdateCommentDto;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public interface CommentService {
    ResponseEntity<?> createComment(CommentDto commentDto, String email);

    ResponseEntity<String> deleteComment(Long commentId, String email);

    ResponseEntity<String> updateComment(Long commentId, UpdateCommentDto updateCommentDto, String  email);
}
