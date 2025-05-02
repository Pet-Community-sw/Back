package com.example.PetApp.service.dogBreed;

import com.example.PetApp.domain.PetBreed;
import com.example.PetApp.repository.jpa.PetBreedRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;


@Service
@RequiredArgsConstructor
public class PetBreedServiceImp implements PetBreedService {

    private final PetBreedRepository petBreedRepository;
    @Override
    public Optional<PetBreed> findByName(String name) {
        return petBreedRepository.findByName(name);
    }

    public Optional<PetBreed> findById(Long id) {
        return petBreedRepository.findById(id);
    }


}
