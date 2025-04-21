package com.example.PetApp.service.dogBreed;

import com.example.PetApp.domain.PetBreed;
import com.example.PetApp.repository.jpa.DogBreedRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;


@Service
@RequiredArgsConstructor
public class DogBreedServiceImp implements DogBreedService {

    private final DogBreedRepository dogBreedRepository;
    @Override
    public Optional<PetBreed> findByName(String name) {
        return dogBreedRepository.findByName(name);
    }

    public Optional<PetBreed> findById(Long id) {
        return dogBreedRepository.findById(id);
    }


}
