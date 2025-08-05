package com.example.PetApp.service.profile;

import com.example.PetApp.domain.*;
import com.example.PetApp.dto.profile.*;
import com.example.PetApp.exception.ConflictException;
import com.example.PetApp.exception.ForbiddenException;
import com.example.PetApp.exception.NotFoundException;
import com.example.PetApp.mapper.ProfileMapper;
import com.example.PetApp.query.MemberQueryService;
import com.example.PetApp.query.PetBreedQueryService;
import com.example.PetApp.query.ProfileQueryService;
import com.example.PetApp.repository.jpa.MemberRepository;
import com.example.PetApp.repository.jpa.ProfileRepository;
import com.example.PetApp.service.dogbreed.PetBreedService;
import com.example.PetApp.service.token.TokenService;
import com.example.PetApp.util.imagefile.FileImageKind;
import com.example.PetApp.util.imagefile.FileUploadUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProfileServiceImpl implements ProfileService {

    @Value("${spring.dog.profile.image.upload}")
    private String profileUploadDir;

    private final ProfileQueryService profileQueryService;
    private final PetBreedQueryService petBreedQueryService;
    private final MemberQueryService memberQueryService;
    private final ProfileRepository profileRepository;
    private final TokenService tokenService;

    @Transactional//accesstoken 수정 필요 이름이 같은지 확인해야됨.
    @Override
    public CreateProfileResponseDto createProfile(ProfileDto profileDto, String email) {
        log.info("createProfile 요청 email : {}", email);
        Member member = memberQueryService.findByMember(email);
        if (profileRepository.countByMember(member) >= 4) {
            throw new ConflictException("프로필은 최대 4개 입니다.");
        }
        PetBreed petBreed = petBreedQueryService.findByPetBreed(profileDto.getPetBreed());

        String imageFileName = FileUploadUtil.fileUpload(profileDto.getPetImageUrl(), profileUploadDir, FileImageKind.PROFILE);
        Profile profile = ProfileMapper.toEntity(profileDto, member, imageFileName, petBreed);
        validateBreed(profileDto, profile);
        profileRepository.save(profile);
        log.info("imageUrl: {}", imageFileName);

        return new CreateProfileResponseDto(profile.getProfileId());
    }


    @Transactional(readOnly = true)
    @Override
    public List<ProfileListResponseDto> getProfiles(String email) {
        log.info("getProfiles 요청 email : {}", email);
        Member member = memberQueryService.findByMember(email);
        List<Profile> profiles = profileRepository.findByMember(member);
        return profiles.stream()
                .map(ProfileMapper::toProfileListResponseDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @Override
    public GetProfileResponseDto getProfile(Long profileId, String email) {
        log.info("getProfile 요청 email : {}, profileId : {}", email, profileId);
        Member member = memberQueryService.findByMember(email);
        Profile profile = profileQueryService.findByProfile(profileId);
        return ProfileMapper.toGetProfileResponseDto(profile, member);
    }


    @Transactional
    @Override
    public void updateProfile(Long profileId, ProfileDto profileDto, String email) {
        log.info("updateProfile 요청 email : {}, profileId : {}", email, profileId);
        Member member = memberQueryService.findByMember(email);
        Profile profile = profileQueryService.findByProfile(profileId);
        PetBreed petBreed = petBreedQueryService.findByPetBreed(profileDto.getPetBreed());

        validateProfile(member, profile.getMember());
        validateBreed(profileDto, profile);
        String imageFimeName = FileUploadUtil.fileUpload(profileDto.getPetImageUrl(), profileUploadDir, FileImageKind.PROFILE);
        ProfileMapper.updateProfile(profile, profileDto, imageFimeName, petBreed);
    }

    @Transactional
    @Override
    public void deleteProfile(Long profileId, String email) {
        log.info("deleteProfile 요청 email : {}, profileId : {}", email, profileId);
        Member member = memberQueryService.findByMember(email);
        Profile profile = profileQueryService.findByProfile(profileId);
        validateProfile(member, profile.getMember());
        profileRepository.deleteById(profileId);
    }

    @Transactional
    @Override
    public AccessTokenByProfileIdResponseDto accessTokenByProfile(String accessToken, String refreshToken, Long profileId, String email) {//요청했을 당시 토큰을 redis에 저장시켜서 이전 토큰으로 요청 시 인증이 안되게 끔 해야됨.
        log.info("accessTokenByProfile 요청 email : {}, profileId : {}", email, profileId);
        Member member = memberQueryService.findByMember(email);
        Profile profile = profileQueryService.findByProfile(profileId);
        validateProfile(member, profile.getMember());
        String newAccessToken = tokenService.newAccessTokenByProfile(accessToken, refreshToken, member, profileId);

        return ProfileMapper.toAccessTokenToProfileIdResponseDto(profileId, newAccessToken);
    }


    private static void validateProfile(Member member, Member profile) {
        if (!(member.equals(profile))) {
            throw new ForbiddenException("권한이 없습니다.");
        }
    }


    private void validateBreed(ProfileDto profileDto, Profile profile) {
        String[] arr = profileDto.getAvoidBreeds().split(",");
        for (String breeds : arr) {
            breeds = breeds.trim();
            PetBreed avoidBreed = petBreedQueryService.findByPetBreed(breeds);
            if (profile.getAvoidBreeds() == null) {
                profile.setAvoidBreeds(new HashSet<>());
            }
            profile.addAvoidBreeds(avoidBreed);
        }
    }
}
