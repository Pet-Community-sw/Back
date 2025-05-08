package com.example.PetApp.controller;


import com.example.PetApp.dto.commment.CommentDto;
import com.example.PetApp.dto.commment.UpdateCommentDto;
import com.example.PetApp.security.jwt.token.JwtAuthenticationToken;
import com.example.PetApp.service.comment.CommentService;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @PostMapping()
    public ResponseEntity<?> createComment(@RequestBody CommentDto commentDto, Authentication authentication) throws JsonProcessingException {
        return commentService.createComment(commentDto, getEmail(authentication));
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<String> deleteComment(@PathVariable Long commentId, Authentication authentication) {
        return commentService.deleteComment(commentId, getEmail(authentication));
    }

    @PutMapping("/{commentId}")//좋아요 개수는 따로하는게 좋을 듯
    public ResponseEntity<String> updateComment(@PathVariable Long commentId, @RequestBody UpdateCommentDto updateCommentDto, Authentication authentication) {
        return commentService.updateComment(commentId,updateCommentDto, getEmail(authentication));
    }

    private String getEmail(Authentication authentication) {
        return authentication.getPrincipal().toString();
    }
}
