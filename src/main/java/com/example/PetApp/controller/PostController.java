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
    public ResponseEntity<?> createPost(@ModelAttribute PostDto createPostDto, Authentication authentication) {
        String email = authentication.getPrincipal().toString();
        return postService.createPost(createPostDto, email);
    }

    @GetMapping("/{postId}")//요청시 댓글까지 한번에 반환. 상세게시물을 보면 무조건 댓글까지 보이게 할거임 그리고 댓글수가 많지않은 커뮤니티라 판단함.
    public ResponseEntity<?> getPost(@PathVariable Long postId, Authentication authentication) {
        String email = authentication.getPrincipal().toString();
        return postService.getPost(postId, email);
    }

    @DeleteMapping("/{postId}")//수정해야됨 profileId전달해야할듯.
    public ResponseEntity<String> deletePost(@PathVariable Long postId, Authentication authentication) {
        String email = authentication.getPrincipal().toString();
        return postService.deletePost(postId, email);
    }

    @PutMapping("/{postId}")
    public ResponseEntity<?> updatePost(@PathVariable Long postId, @ModelAttribute PostDto postDto, Authentication authentication) {
        String email = authentication.getPrincipal().toString();
        return postService.updatePost(postId, postDto, email);
    }
}
