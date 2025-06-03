package com.example.PetApp.repository.jpa;

import com.example.PetApp.domain.PetBreed;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PetBreedRepository extends JpaRepository<PetBreed, Long> {

    Optional<PetBreed> findByName(String name);
}
