package com.example.PetApp.service;

import com.example.PetApp.domain.Member;
import com.example.PetApp.domain.Profile;
import com.example.PetApp.dto.profile.AddProfileDto;
import com.example.PetApp.repository.MemberRepository;
import com.example.PetApp.repository.ProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProfileServiceImp implements ProfileService {

    @Value("${spring.dog.profile.image.upload}")
    private String uploadDir;

    private final ProfileRepository profileRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public Profile addProfile(AddProfileDto addProfileDto,String email) {
        Member member = memberRepository.findByEmail(email).get();

        MultipartFile file = addProfileDto.getFile();

        UUID uuid = UUID.randomUUID();
        String imageFileName = uuid + "_" + file.getOriginalFilename();

        try {
            // 파일 저장
            Path path = Paths.get(uploadDir, imageFileName);
            Files.copy(file.getInputStream(), path);

            // 새로운 프로필 생성
            Profile profile = Profile.builder()
                    .memberId(member.getMemberId())
                    .imageUrl("/profile/" + imageFileName)
                    .dogBreed(addProfileDto.getDogBreed())
                    .name(addProfileDto.getName())
                    .build();

            return profileRepository.save(profile);
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
    }

    @Transactional
    @Override
    public List<Profile> profileList(String email) {
        Member member = memberRepository.findByEmail(email).get();
        return  profileRepository.findByMemberId(member.getMemberId());
    }

    @Transactional
    @Override
    public ResponseEntity getProfile(Long profileId) {
        Optional<Profile> profile = profileRepository.findById(profileId);
        if (profile.isPresent()) {
            return ResponseEntity.ok().body(profile);
        }else
            return ResponseEntity.badRequest().body("해당 프로필은 없습니다.");
    }

    @Override
    public Long getCount(String email) {
        Member member = memberRepository.findByEmail(email).get();//이미 인증된 사용자이기 때문에.
        return profileRepository.countByMemberId(member.getMemberId());
    }


}
