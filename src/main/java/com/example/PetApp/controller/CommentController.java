package com.example.PetApp.controller;


import com.example.PetApp.dto.commment.CommentDto;
import com.example.PetApp.dto.commment.UpdateCommentDto;
import com.example.PetApp.service.comment.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @GetMapping("/{commentId}")
    public ResponseEntity<Object> getComment(@PathVariable Long commentId, Authentication authentication) {
        String email = authentication.getPrincipal().toString();
        return commentService.getComment(commentId, email);
    }

    @PostMapping()
    public ResponseEntity<Object> createComment(@RequestBody CommentDto commentDto, Authentication authentication) {
        String email = authentication.getPrincipal().toString();
        return commentService.createComment(commentDto, email);
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<String> deleteComment(@PathVariable Long commentId, Authentication authentication) {
        String email = authentication.getPrincipal().toString();
        return commentService.deleteComment(commentId, email);
    }

    @PutMapping("/{commentId}")//좋아요 개수는 따로하는게 좋을 듯
    public ResponseEntity<String> updateComment(@PathVariable Long commentId, @RequestBody UpdateCommentDto updateCommentDto, Authentication authentication) {
        String email = authentication.getPrincipal().toString();
        return commentService.updateComment(commentId, updateCommentDto.getContent(), email);
    }
}
