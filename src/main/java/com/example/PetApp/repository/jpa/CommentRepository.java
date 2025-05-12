package com.example.PetApp.repository.jpa;

import com.example.PetApp.domain.Comment;
import com.example.PetApp.domain.RecommendRoutePost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findAllByRecommendRoutePost(RecommendRoutePost recommendRoutePost);
}
