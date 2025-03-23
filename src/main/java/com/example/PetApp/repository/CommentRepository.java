package com.example.PetApp.repository;

import com.example.PetApp.domain.Comment;
import com.example.PetApp.dto.commment.GetCommentsResponseDto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> findAllByPostId(Long postId);
}
