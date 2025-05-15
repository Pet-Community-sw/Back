package com.example.PetApp.controller;


import com.example.PetApp.dto.like.LikeDto;
import com.example.PetApp.security.jwt.token.JwtAuthenticationToken;
import com.example.PetApp.service.like.LikeService;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/likes")
@RequiredArgsConstructor
public class LikeController {

    private final LikeService likeService;

    @GetMapping("/post/{postId}")//recommendRoutePost따로 보내야할듯
    public ResponseEntity<?> getLike(@PathVariable Long postId) {
        return likeService.getLike(postId);
    }

    //이상함
    @GetMapping("/recommend-route-post/{recommendRoutePostId}")
    private ResponseEntity<?> getLikeByRecommendRoutePostId(@PathVariable Long recommendRoutePostId) {
        return likeService.getLikeByRecommendRoutePostId(recommendRoutePostId);
    }

    @PostMapping()
    public ResponseEntity<?> createAndDeleteLike(@RequestBody LikeDto likeDto, Authentication authentication) throws JsonProcessingException {
        String email = authentication.getPrincipal().toString();
        return likeService.createAndDeleteLike(likeDto, email);
    }
}
