package com.example.PetApp.controller;


import com.example.PetApp.dto.like.LikeDto;
import com.example.PetApp.dto.like.LikeResponseDto;
import com.example.PetApp.service.comment.PostType;
import com.example.PetApp.service.like.LikeService;
import com.example.PetApp.util.AuthUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/likes")
@RequiredArgsConstructor
public class LikeController {

    private final LikeService likeService;

    @GetMapping("/{postId}")//api 명세서 수정해야함.
    public LikeResponseDto getLikes(@PathVariable Long postId) {
        return likeService.getLikes(postId);
    }

    @PostMapping()
    public ResponseEntity<?> createAndDeleteLike(@RequestBody Long postId, Authentication authentication) {
        return likeService.createAndDeleteLike(postId, AuthUtil.getEmail(authentication));
    }
}
