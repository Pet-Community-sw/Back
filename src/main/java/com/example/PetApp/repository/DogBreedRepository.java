package com.example.PetApp.repository;

import com.example.PetApp.domain.DogBreed;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DogBreedRepository extends JpaRepository<DogBreed, Long> {

    Optional<DogBreed> findByName(String name);
}
