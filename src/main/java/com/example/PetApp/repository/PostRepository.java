package com.example.PetApp.repository;

import com.example.PetApp.domain.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post,Long> {
    Page<Post> findByOrderByRegdateDesc(Pageable pageable);
}
