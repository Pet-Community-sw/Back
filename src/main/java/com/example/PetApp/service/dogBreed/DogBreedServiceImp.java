package com.example.PetApp.service.dogBreed;

import com.example.PetApp.domain.DogBreed;
import com.example.PetApp.repository.jpa.DogBreedRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;


@Service
@RequiredArgsConstructor
public class DogBreedServiceImp implements DogBreedService {

    private final DogBreedRepository dogBreedRepository;
    @Override
    public Optional<DogBreed> findByName(String name) {
        return dogBreedRepository.findByName(name);
    }

    public Optional<DogBreed> findById(Long id) {
        return dogBreedRepository.findById(id);
    }


}
