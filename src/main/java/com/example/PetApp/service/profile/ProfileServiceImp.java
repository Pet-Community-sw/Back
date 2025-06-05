package com.example.PetApp.service.profile;

import com.example.PetApp.domain.*;
import com.example.PetApp.dto.profile.*;
import com.example.PetApp.exception.ConflictException;
import com.example.PetApp.exception.ForbiddenException;
import com.example.PetApp.exception.NotFoundException;
import com.example.PetApp.mapper.ProfileMapper;
import com.example.PetApp.repository.jpa.MemberRepository;
import com.example.PetApp.repository.jpa.ProfileRepository;
import com.example.PetApp.security.jwt.util.JwtTokenizer;
import com.example.PetApp.service.dogbreed.PetBreedService;
import com.example.PetApp.util.RedisUtil;
import com.example.PetApp.util.imagefile.FileImageKind;
import com.example.PetApp.util.imagefile.FileUploadUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.MonthDay;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProfileServiceImp implements ProfileService {

    @Value("${spring.dog.profile.image.upload}")
    private String profileUploadDir;

    private final ProfileRepository profileRepository;
    private final MemberRepository memberRepository;
    private final PetBreedService petBreedService;
    private final JwtTokenizer jwtTokenizer;
    private final RedisUtil redisUtil;

    @Transactional//accesstoken 수정 필요 이름이 같은지 확인해야됨.
    @Override
    public CreateProfileResponseDto createProfile(ProfileDto profileDto, String email) {
        log.info("addProfile 요청 email : {}", email);
        Member member = memberRepository.findByEmail(email).get();
        if (profileRepository.countByMember(member) >= 4) {
            throw new ConflictException("프로필은 최대 4개 입니다.");
        }
        petBreedService.findByName(profileDto.getPetBreed())
                .orElseThrow(() -> new NotFoundException("종을 다시 입력해주세요."));

        String imageFileName = FileUploadUtil.fileUpload(profileDto.getPetImageUrl(), profileUploadDir, FileImageKind.PROFILE);
        Profile profile = ProfileMapper.toEntity(profileDto, member, imageFileName);
        validateBreed(profileDto, profile);
        Profile addProfile = profileRepository.save(profile);
        return new CreateProfileResponseDto(addProfile.getProfileId());
    }


    @Transactional(readOnly = true)
    @Override
    public List<ProfileListResponseDto> getProfiles(String email) {
        log.info("getProfiles 요청 email : {}", email);
        Member member = memberRepository.findByEmail(email).get();
        List<Profile> profiles = profileRepository.findByMember(member);
        return profiles.stream()
                .map(ProfileMapper::toProfileListResponseDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @Override
    public GetProfileResponseDto getProfile(Long profileId, String email) {
        log.info("getProfile 요청 email : {}, profileId : {}", email, profileId);
        Member member = memberRepository.findByEmail(email).get();
        Profile profile = profileRepository.findById(profileId)
                .orElseThrow(() -> new NotFoundException("해당 프로필은 없습니다."));
        return ProfileMapper.toGetProfileResponseDto(profile, member);
    }


    @Transactional
    @Override
    public void updateProfile(Long profileId, ProfileDto profileDto, String email) {
        log.info("updateProfile 요청 email : {}, profileId : {}", email, profileId);
        Member member = memberRepository.findByEmail(email).get();
        Profile profile = profileRepository.findById(profileId)
                .orElseThrow(() -> new NotFoundException("해당 프로필은 없습니다."));
        petBreedService.findByName(profileDto.getPetBreed())
                .orElseThrow(() -> new NotFoundException("종을 다시 입력해주세요."));

        if (!(member.equals(profile.getMember()))) {
            throw new ForbiddenException("수정 권한 없습니다.");
        }
        validateBreed(profileDto, profile);
        String imageFimeName = FileUploadUtil.fileUpload(profileDto.getPetImageUrl(), profileUploadDir, FileImageKind.PROFILE);
        ProfileMapper.updateProfile(profile, profileDto, imageFimeName);
    }

    @Transactional
    @Override
    public void deleteProfile(Long profileId, String email) {
        log.info("deleteProfile 요청 email : {}, profileId : {}", email, profileId);
        Member member = memberRepository.findByEmail(email).get();
        Profile profile = profileRepository.findById(profileId)
                .orElseThrow(() -> new NotFoundException("해당 프로필은 없습니다."));
            if (!(member.equals(profile.getMember()))) {
                throw new ForbiddenException("삭제 권한이 없음.");
            }
        profileRepository.deleteById(profileId);
    }

    @Transactional
    @Override
    public AccessTokenByProfileIdResponseDto accessTokenByProfile(String accessToken, Long profileId, String email) {//요청했을 당시 토큰을 redis에 저장시켜서 이전 토큰으로 요청 시 인증이 안되게 끔 해야됨.
        log.info("accessTokenByProfile 요청 email : {}, profileId : {}", email, profileId);
        Profile profile = profileRepository.findById(profileId)
                .orElseThrow(() -> new NotFoundException("해당 프로필은 없습니다."));
        Member member = memberRepository.findByEmail(email).get();
        if (!(profile.getMember().equals(member))) {
            throw new ForbiddenException("권한이 없습니다.");
        }
        redisUtil.createData(accessToken, "blacklist", 30 * 60L);//에세스토큰 유효시간
        String newAccessToken = jwtTokenizer.createAccessToken(member.getMemberId(), profileId, member.getEmail());
        return ProfileMapper.toAccessTokenToProfileIdResponseDto(profileId, newAccessToken);
    }


    private void validateBreed(ProfileDto profileDto, Profile profile) {
        String[] arr = profileDto.getAvoidBreeds().split(",");
        for (String breeds : arr) {
            breeds = breeds.trim();
            PetBreed avoidBreed = petBreedService.findByName(breeds)
                    .orElseThrow(() -> new NotFoundException("피해야하는 종을 다시 입력해주세요."));
            if (profile.getAvoidBreeds() == null) {
                profile.setAvoidBreeds(new HashSet<>());
            }
            profile.addAvoidBreeds(avoidBreed);
        }
    }
}
