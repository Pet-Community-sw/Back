package com.example.PetApp.query;

import com.example.PetApp.domain.Profile;
import com.example.PetApp.exception.ForbiddenException;
import com.example.PetApp.repository.jpa.ProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProfileQueryService {

    private final ProfileRepository profileRepository;

    public Profile findByProfile(Long profileId) {
        return profileRepository.findById(profileId).orElseThrow(() -> new ForbiddenException("프로필을 등록해주세요."));
    }
}
