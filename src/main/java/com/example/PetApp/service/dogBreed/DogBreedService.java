package com.example.PetApp.service.dogBreed;

import com.example.PetApp.domain.PetBreed;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public interface DogBreedService {

    Optional<PetBreed> findByName(String name);

    Optional<PetBreed> findById(Long id);

}
