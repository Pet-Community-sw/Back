package com.example.PetApp.service;

import com.example.PetApp.domain.Profile;
import com.example.PetApp.dto.profile.AddProfileDto;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public interface ProfileService {
    Profile addProfile(AddProfileDto addProfileDto,String email);

    List<Profile> profileList(String email);

    ResponseEntity getProfile(Long profileId);

    Long getCount(String email);
}
