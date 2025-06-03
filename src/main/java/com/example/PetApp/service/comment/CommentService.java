package com.example.PetApp.service.comment;

import com.example.PetApp.dto.commment.CommentDto;
import com.example.PetApp.dto.commment.CreateCommentResponseDto;
import com.example.PetApp.dto.commment.GetCommentsResponseDto;
import com.example.PetApp.dto.commment.UpdateCommentDto;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface CommentService {
    CreateCommentResponseDto createComment(CommentDto commentDto, String email);

    void deleteComment(Long commentId, String email);

    void updateComment(Long commentId, UpdateCommentDto updateCommentDto, String  email);

    List<GetCommentsResponseDto> getComments(Long recommendRoutePostId, String email);
}
