package com.example.PetApp.repository.jpa;

import com.example.PetApp.domain.post.Post;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post,Long> {


}
