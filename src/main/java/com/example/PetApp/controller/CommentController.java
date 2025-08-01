package com.example.PetApp.controller;


import com.example.PetApp.dto.MessageResponse;
import com.example.PetApp.dto.commment.CommentDto;
import com.example.PetApp.dto.commment.CreateCommentResponseDto;
import com.example.PetApp.dto.commment.GetCommentsResponseDto;
import com.example.PetApp.dto.commment.UpdateCommentDto;
import com.example.PetApp.service.comment.CommentService;
import com.example.PetApp.util.AuthUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @GetMapping("/{postId}")
    private List<GetCommentsResponseDto> getComments(@PathVariable Long postId, Authentication authentication) {
        return commentService.getComments(postId, AuthUtil.getEmail(authentication));
    }

    @PostMapping()
    public CreateCommentResponseDto createComment(@RequestBody @Valid CommentDto commentDto, Authentication authentication) {
        return commentService.createComment(commentDto, AuthUtil.getEmail(authentication));
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<MessageResponse> deleteComment(@PathVariable Long commentId, Authentication authentication) {
        commentService.deleteComment(commentId, AuthUtil.getEmail(authentication));
        return ResponseEntity.ok(new MessageResponse("삭제 되었습니다."));
    }

    @PutMapping("/{commentId}")//좋아요 개수는 따로하는게 좋을 듯
    public ResponseEntity<MessageResponse> updateComment(@PathVariable Long commentId, @RequestBody @Valid UpdateCommentDto updateCommentDto, Authentication authentication) {
        commentService.updateComment(commentId,updateCommentDto, AuthUtil.getEmail(authentication));
        return ResponseEntity.ok(new MessageResponse("수정 되었습니다."));
    }
}
