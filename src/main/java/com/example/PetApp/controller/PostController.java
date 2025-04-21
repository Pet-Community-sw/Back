package com.example.PetApp.controller;

import com.example.PetApp.dto.post.PostDto;
import com.example.PetApp.dto.post.PostListResponseDto;
import com.example.PetApp.security.jwt.token.JwtAuthenticationToken;
import com.example.PetApp.service.post.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @GetMapping()
    public ResponseEntity<List<PostListResponseDto>> getPosts(@RequestParam(defaultValue = "0") int page) {
        List<PostListResponseDto> postList = postService.getPosts(page);
        return ResponseEntity.ok().body(postList);
    }

    @PostMapping()
    public ResponseEntity<Object> createPost(@ModelAttribute PostDto createPostDto, Authentication authentication) {
        JwtAuthenticationToken jwtAuthenticationToken = (JwtAuthenticationToken) authentication;
        Long profileId = jwtAuthenticationToken.getProfileId();
        System.out.println("profileId = " + profileId);
        return postService.createPost(createPostDto, profileId);
    }

    @GetMapping("/{postId}")
    public ResponseEntity<Object> getPost(@PathVariable Long postId, Authentication authentication) {
        JwtAuthenticationToken jwtAuthenticationToken = (JwtAuthenticationToken) authentication;
        Long profileId = jwtAuthenticationToken.getProfileId();
        return postService.getPost(postId, profileId);
    }

    @DeleteMapping("/{postId}")//수정해야됨 profileId전달해야할듯.
    public ResponseEntity<String> deletePost(@PathVariable Long postId, Authentication authentication) {
        JwtAuthenticationToken jwtAuthenticationToken = (JwtAuthenticationToken) authentication;
        String email = jwtAuthenticationToken.getPrincipal().toString();
        Long profileId = jwtAuthenticationToken.getProfileId();
        return postService.deletePost(postId, email);
    }

    @PutMapping("/{postId}")
    public ResponseEntity<Object> updatePost(@PathVariable Long postId, @ModelAttribute PostDto postDto, Authentication authentication) {
        JwtAuthenticationToken jwtAuthenticationToken = (JwtAuthenticationToken) authentication;
        String email = jwtAuthenticationToken.getPrincipal().toString();
        Long profileId = jwtAuthenticationToken.getProfileId();
        return postService.updatePost(postId, postDto, profileId, email);
    }
}
