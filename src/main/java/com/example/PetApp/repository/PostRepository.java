package com.example.PetApp.repository;

import com.example.PetApp.domain.Post;
import com.example.PetApp.projection.PostProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post,Long> {
//    @Query("SELECT p.profile.dogName AS dogName, p.profile.imageUrl AS imageUrl, p.title AS title, p.content AS content, p.likeCount AS likeCount " +
//            "FROM Post p")
    Page<Post> findByOrderByRegdateDesc(Pageable pageable);
}
