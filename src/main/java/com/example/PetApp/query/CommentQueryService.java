package com.example.PetApp.query;

import com.example.PetApp.domain.Comment;
import com.example.PetApp.exception.NotFoundException;
import com.example.PetApp.repository.jpa.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CommentQueryService {

    private final CommentRepository commentRepository;

    public Comment findByComment(Long commentId) {
        return commentRepository.findById(commentId).orElseThrow(() -> new NotFoundException("해당 댓글은 없습니다."));
    }
}
