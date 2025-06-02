package com.example.PetApp.service.dogbreed;

import com.example.PetApp.domain.PetBreed;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public interface PetBreedService {

    Optional<PetBreed> findByName(String name);

    Optional<PetBreed> findById(Long id);

}
