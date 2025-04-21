package com.example.PetApp.repository.jpa;

import com.example.PetApp.domain.PetBreed;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DogBreedRepository extends JpaRepository<PetBreed, Long> {

    Optional<PetBreed> findByName(String name);
}
