package com.example.PetApp.service;

import com.example.PetApp.dto.post.PostDto;
import com.example.PetApp.dto.post.GetUpdatePostResponseDto;
import com.example.PetApp.dto.post.PostListResponseDto;
import com.example.PetApp.dto.post.UpdateLikeDto;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
public interface PostService {

    List<PostListResponseDto> getPosts(int page);

    ResponseEntity<Object> createPost(PostDto createPostDto);

    ResponseEntity<Object> getPost(Long postId, String email);

    ResponseEntity<String> deletePost(Long postId, String email);

    ResponseEntity<Object> updatePost(Long postId, PostDto postDto, String email);

    ResponseEntity<Object> updateLike(UpdateLikeDto updateLikeDto, String email);
}
