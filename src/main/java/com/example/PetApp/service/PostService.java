package com.example.PetApp.service;

import com.example.PetApp.dto.post.PostDto;
import com.example.PetApp.dto.post.PostListResponseDto;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface PostService {

    List<PostListResponseDto> getPosts(int page);

    ResponseEntity<Object> createPost(PostDto createPostDto, String email);

    ResponseEntity<Object> getPost(Long postId, String email);

    ResponseEntity<String> deletePost(Long postId, String email);

    ResponseEntity<Object> updatePost(Long postId, PostDto postDto, String email);
}
