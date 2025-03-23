package com.example.PetApp.controller;

import com.example.PetApp.dto.post.CreatePostDto;
import com.example.PetApp.dto.post.GetPostResponseDto;
import com.example.PetApp.dto.post.PostListResponseDto;
import com.example.PetApp.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<?> createPost(@ModelAttribute CreatePostDto createPostDto) {
        return postService.createPost(createPostDto);
    }

    @GetMapping("/{postId}")
    public ResponseEntity<GetPostResponseDto> getPost(@PathVariable Long postId) {
        return postService.getPost(postId);
    }
}
