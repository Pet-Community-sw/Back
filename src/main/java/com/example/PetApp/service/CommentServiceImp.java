package com.example.PetApp.service;

import com.example.PetApp.domain.Comment;
import com.example.PetApp.domain.Profile;
import com.example.PetApp.dto.commment.CreateCommentDto;
import com.example.PetApp.dto.commment.GetCommentsResponseDto;
import com.example.PetApp.dto.post.PostListResponseDto;
import com.example.PetApp.repository.CommentRepository;
import com.example.PetApp.repository.ProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CommentServiceImp implements CommentService {

    private final CommentRepository commentRepository;
    private final ProfileRepository profileRepository;

    @Override
    public ResponseEntity<?> createComment(CreateCommentDto createCommentDto) {
        Optional<Profile> profile = profileRepository.findById(createCommentDto.getProfileId());
        if (profile.isEmpty()) {
            return ResponseEntity.badRequest().body("해당 프로필은 없습니다.");
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
