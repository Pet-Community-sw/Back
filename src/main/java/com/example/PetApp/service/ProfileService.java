package com.example.PetApp.service;

import com.example.PetApp.domain.Profile;
import com.example.PetApp.dto.profile.AddProfileDto;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
public interface ProfileService {
    Profile addProfile(AddProfileDto addProfileDto,String email);
}
