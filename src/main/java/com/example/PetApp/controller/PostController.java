package com.example.PetApp.controller;

import com.example.PetApp.dto.post.PostDto;
import com.example.PetApp.dto.post.GetUpdatePostResponseDto;
import com.example.PetApp.dto.post.PostListResponseDto;
import com.example.PetApp.service.PostService;
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
    public ResponseEntity<Object> createPost(@ModelAttribute PostDto createPostDto) {
        return postService.createPost(createPostDto);
    }

    @GetMapping("/{postId}")
    public ResponseEntity<Object> getPost(@PathVariable Long postId, Authentication authentication) {
        String email = authentication.getPrincipal().toString();
        return postService.getPost(postId, email);
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<String> deletePost(@PathVariable Long postId, Authentication authentication) {
        String email = authentication.getPrincipal().toString();
        return postService.deletePost(postId, email);
    }

    @PutMapping("/{postId}")
    public ResponseEntity<Object> updatePost(@PathVariable Long postId, @ModelAttribute PostDto postDto, Authentication authentication) {
        String email = authentication.getPrincipal().toString();
        return postService.updatePost(postId, postDto, email);
    }
}
