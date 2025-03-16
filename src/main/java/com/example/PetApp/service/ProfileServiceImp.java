package com.example.PetApp.service;

import com.example.PetApp.domain.Member;
import com.example.PetApp.domain.Profile;
import com.example.PetApp.dto.profile.AddProfileDto;
import com.example.PetApp.repository.MemberRepository;
import com.example.PetApp.repository.ProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProfileServiceImp implements ProfileService {

    @Value("${spring.dog.profile.image.upload}")
    private String uploadDir;

    private final ProfileRepository profileRepository;
    private final MemberRepository memberRepository;

    public Profile addProfile(AddProfileDto addProfileDto,String email) {
        // 회원 정보 조회
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("이메일이 존재하지 않습니다."));

        MultipartFile file = addProfileDto.getFile();

        // 파일 이름 생성 (UUID 사용)
        UUID uuid = UUID.randomUUID();
        String imageFileName = uuid + "_" + file.getOriginalFilename();

        try {
            // 파일 저장
            Path savePath = Paths.get(uploadDir, imageFileName);
            Files.copy(file.getInputStream(), savePath);

            // 새로운 프로필 생성
            Profile profile = Profile.builder()
                    .member(member)  // Member 엔티티
                    .imageUrl("/profile/" + imageFileName)
                    .dogBreed(addProfileDto.getDogBreed())
                    .name(addProfileDto.getName())
                    .build();

            // 프로필 저장
            return profileRepository.save(profile);
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
    }
}
