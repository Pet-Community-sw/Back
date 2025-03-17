package com.example.PetApp.service;

import com.example.PetApp.domain.Member;
import com.example.PetApp.domain.Profile;
import com.example.PetApp.dto.profile.ProfileDto;
import com.example.PetApp.dto.profile.GetProfileResponseDto;
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

@Service
@RequiredArgsConstructor
public class ProfileServiceImp implements ProfileService {

    @Value("${spring.dog.profile.image.upload}")
    private String uploadDir;

    private final ProfileRepository profileRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public Profile addProfile(ProfileDto addProfileDto, String email) {
        Member member = getMember(email);

        MultipartFile file = addProfileDto.getFile();

        try {
            // 파일 저장
            Path path = Paths.get(uploadDir, file.getOriginalFilename());
            Files.copy(file.getInputStream(), path);

            // 새로운 프로필 생성
            Profile profile = Profile.builder()
                    .memberId(member.getMemberId())
                    .imageUrl("/profile/" + file.getOriginalFilename())
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
        Member member = getMember(email);
        return  profileRepository.findByMemberId(member.getMemberId());
    }

    @Transactional
    @Override
    public ResponseEntity getProfile(Long profileId, String email) {
        Member member = getMember(email);
        Optional<Profile> profile = profileRepository.findById(profileId);
        if (profile.isPresent()) {
            GetProfileResponseDto getProfileResponseDto = GetProfileResponseDto.builder()
                    .profileId(profile.get().getProfileId())
                    .dogBreed(profile.get().getDogBreed())
                    .imageUrl(profile.get().getImageUrl())
                    .memberId(profile.get().getMemberId())
                    .name(profile.get().getName())
                    .isOwner(false)
                    .build();
            if (member.getMemberId() == profile.get().getMemberId()) {
                getProfileResponseDto.setOwner(true);
            }
            return ResponseEntity.ok().body(getProfileResponseDto);
        } else {
            return ResponseEntity.badRequest().body("해당 프로필은 없습니다.");
        }
    }

    @Transactional
    @Override
    public ResponseEntity updateProfile(Long profileId, ProfileDto addProfileDto, String email) {
        Member member = getMember(email);
        Optional<Profile> profile = profileRepository.findById(profileId);
        if (profile.isEmpty()) {
            return ResponseEntity.badRequest().body("없는 프로필입니다.");
        } else {
            if (member.getMemberId() == profile.get().getMemberId()) {
                profile.get().setImageUrl(addProfileDto.getFile().getOriginalFilename());
                profile.get().setDogBreed(addProfileDto.getDogBreed());
                profile.get().setName(addProfileDto.getName());
                return ResponseEntity.ok().body(profile);
            }else {
                return ResponseEntity.badRequest().body("수정 권한이 없습니다.");
            }

        }

    }

    @Transactional
    @Override
    public ResponseEntity deleteByProfileId(Long profileId, String email) {
        Member member = getMember(email);
        Optional<Profile> profile = profileRepository.findById(profileId);
        if (profile.isPresent()) {
            if (member.getMemberId() == profile.get().getMemberId()) {
                profileRepository.deleteById(profileId);
                return ResponseEntity.ok().body("삭제 되었습니다.");
            } else {
                return ResponseEntity.badRequest().body("삭제 권한이 없습니다.");
            }
        }else {
            return ResponseEntity.badRequest().body("없는 프로필입니다.");
        }
    }

    @Transactional
    @Override
    public Long getCount(String email) {
        Member member = getMember(email);
        return profileRepository.countByMemberId(member.getMemberId());
    }

    private Member getMember(String email) {
        return memberRepository.findByEmail(email).get();
    }

}
