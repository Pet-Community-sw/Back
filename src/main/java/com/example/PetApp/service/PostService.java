package com.example.PetApp.service;

import com.example.PetApp.domain.Post;
import com.example.PetApp.dto.post.CreatePostDto;
import com.example.PetApp.dto.post.PostListResponseDto;
import com.example.PetApp.projection.PostProjection;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
public interface PostService {

    List<PostListResponseDto> getPosts(int page);

    ResponseEntity<Long> createPost(CreatePostDto createPostDto);

    ResponseEntity<Post> getPost(Long postId);
}
