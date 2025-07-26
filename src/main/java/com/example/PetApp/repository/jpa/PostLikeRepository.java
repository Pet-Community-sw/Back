package com.example.PetApp.repository.jpa;

import com.example.PetApp.domain.Post;
import com.example.PetApp.domain.like.PostLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostLikeRepository extends JpaRepository<PostLike, Long> {

    List<PostLike> findAllByPost(Post post);
}
