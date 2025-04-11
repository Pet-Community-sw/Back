package com.example.PetApp.service.comment;

import com.example.PetApp.dto.commment.CommentDto;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public interface CommentService {
    ResponseEntity<Object> createComment(CommentDto commentDto, Long profileId);

    ResponseEntity<Object> getComment(Long commentId, Long profileId);

    ResponseEntity<String> deleteComment(Long commentId, Long profileId);

    ResponseEntity<String> updateComment(Long commentId, String content, Long profileId);
}
