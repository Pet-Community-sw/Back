package com.example.PetApp.repository.jpa;

import com.example.PetApp.domain.WalkerPost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WalkerPostRepository extends JpaRepository<WalkerPost, Long> {

}
