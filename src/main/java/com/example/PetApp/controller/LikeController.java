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

    @GetMapping("/{postType}/{postId}")
    public LikeResponseDto getLikes(@PathVariable PostType postType, @PathVariable Long postId) {
        return likeService.getLikes(postType, postId);
    }

    @PostMapping()
    public ResponseEntity<?> createAndDeleteLike(@RequestBody @Valid LikeDto likeDto, Authentication authentication) {
        return likeService.createAndDeleteLike(likeDto, AuthUtil.getEmail(authentication));
    }
}
