package com.example.PetApp.controller;


import com.example.PetApp.dto.commment.CreateCommentDto;
import com.example.PetApp.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    //모든 GET 요청은 Body로 보내면 안된다!
    @GetMapping("/{commentId}")
    public ResponseEntity<Object> getComment(@PathVariable Long commentId, Authentication authentication) {
        String email = authentication.getPrincipal().toString();
        return commentService.getComment(commentId, email);
    }

    @PostMapping()
    public ResponseEntity<Object> createComment(@RequestBody CreateCommentDto createCommentDto, Authentication authentication) {
        String email = authentication.getPrincipal().toString();
        return commentService.createComment(createCommentDto, email);
    }

//    @PutMapping("/{commentId}")//좋아요 개수는 따로하는게 좋을 듯
//    public ResponseEntity<?> updateComment(@PathVariable Long commentId, Authentication authentication) {
//        String email = authentication.getPrincipal().toString();
//        return commentService.updateComment(commentId, email);
//    }
}
