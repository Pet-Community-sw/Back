package com.example.PetApp.service;

import com.example.PetApp.domain.Comment;
import com.example.PetApp.domain.Post;
import com.example.PetApp.domain.Profile;
import com.example.PetApp.dto.commment.CreateCommentDto;
import com.example.PetApp.dto.commment.GetCommentsResponseDto;
import com.example.PetApp.dto.post.PostListResponseDto;
import com.example.PetApp.repository.CommentRepository;
import com.example.PetApp.repository.PostRepository;
import com.example.PetApp.repository.ProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CommentServiceImp implements CommentService {

    private final CommentRepository commentRepository;
    private final ProfileRepository profileRepository;
    private final PostRepository postRepository;

    @Override
    public ResponseEntity<Object> createComment(CreateCommentDto createCommentDto) {
        Optional<Profile> profile = profileRepository.findById(createCommentDto.getProfileId());
        Optional<Post> post = postRepository.findById(createCommentDto.getPostId());
        if (profile.isEmpty()||post.isEmpty()) {
            return ResponseEntity.badRequest().body("해당 프로필 혹은 해당 게시물이 없습니다.");
        }
        Comment comment=Comment.builder()
                .content(createCommentDto.getContent())
                .postId(createCommentDto.getPostId())
                .likeCount(createCommentDto.getLikeCount())
                .profile(profile.get())
                .build();
        Comment newComment = commentRepository.save(comment);
        return ResponseEntity.ok(newComment.getCommentId());
    }


}
