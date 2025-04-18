package com.example.PetApp.controller;


import com.example.PetApp.dto.like.LikeDto;
import com.example.PetApp.security.jwt.token.JwtAuthenticationToken;
import com.example.PetApp.service.like.LikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/likes")
@RequiredArgsConstructor
public class LikeController {

    private final LikeService likeService;

    @GetMapping("/{postId}")//좋아요 갯수만 보내줘도 되나요? 좋아요 누를 때마다 페이지 새로고침하는거면 너무 성능이 낭비일 것 같은디...
    public ResponseEntity<Object> getLike(@PathVariable Long postId) {
        return likeService.getLike(postId);
    }
    //이상함

    @PostMapping()
    public ResponseEntity<Object> createAndDeleteLike(@RequestBody LikeDto likeDto, Authentication authentication) {
        JwtAuthenticationToken authentication1 = (JwtAuthenticationToken) authentication;
        Long profileId = authentication1.getProfileId();
        return likeService.createAndDeleteLike(likeDto, profileId);
    }
}
