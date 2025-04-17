package com.example.PetApp.service.profile;

import com.example.PetApp.domain.*;
import com.example.PetApp.dto.profile.*;
import com.example.PetApp.repository.jpa.MemberRepository;
import com.example.PetApp.repository.jpa.ProfileRepository;
import com.example.PetApp.repository.jpa.RefreshRepository;
import com.example.PetApp.security.jwt.util.JwtTokenizer;
import com.example.PetApp.service.dogBreed.DogBreedService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.MonthDay;
import java.time.Period;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProfileServiceImp implements ProfileService {

    @Value("${spring.dog.profile.image.upload}")
    private String profileUploadDir;

    private final ProfileRepository profileRepository;
    private final MemberRepository memberRepository;
    private final DogBreedService dogBreedService;
    private final JwtTokenizer jwtTokenizer;
    private final RefreshRepository refreshRepository;

    @Transactional
    public ResponseEntity addProfile(ProfileDto profileDto, String email) {
        Member member = getMember(email);

        MultipartFile file = profileDto.getProfileImageFile();

        UUID uuid = UUID.randomUUID();
        String imageFileName = uuid + "_" + file.getOriginalFilename();

        Optional<DogBreed> dogBreed = dogBreedService.findByName(profileDto.getDogBreed());
        if (dogBreed.isEmpty()) {
            return ResponseEntity.badRequest().body("종을 다시 입력해주세요.");
        }
        try {
            Path path = Paths.get(profileUploadDir, imageFileName);
            Files.copy(file.getInputStream(), path);

            Profile profile = Profile.builder()
                    .memberId(member.getMemberId())
                    .imageUrl("/profile/" + imageFileName)
                    .dogBirthDate(profileDto.getDogBirthDate())
                    .extraInfo(profileDto.getExtraInfo())
                    .dogBreed(profileDto.getDogBreed())
                    .dogAge(CalculateAge(profileDto.getDogBirthDate())+"살")
                    .dogName(profileDto.getDogName())
                    .build();
            String[] arr = profileDto.getAvoidBreeds().split(",");
            for (String breeds : arr) {
                breeds=breeds.trim();
                Optional<DogBreed> avoidBreed = dogBreedService.findByName(breeds);
                if (avoidBreed.isEmpty()) {
                    return ResponseEntity.badRequest().body("피해야하는 종을 다시 입력해주세요.");
                }
                if (profile.getAvoidBreeds() == null) {
                    profile.setAvoidBreeds(new HashSet<>());
                }
                profile.addAvoidBreeds(avoidBreed.get());
            }
            Profile addProfile = profileRepository.save(profile);
            List<String> roles = member.getRoles().stream().map(Role::getName).collect(Collectors.toList());

            String accessToken = jwtTokenizer.createAccessToken(member.getMemberId(), addProfile.getProfileId(), member.getEmail(), roles);//accessToken 추가
            //refresh는 노.
            AddProfileResponseDto addProfileResponseDto=AddProfileResponseDto.builder()//profileId넣어서 토큰 반환.
                    .accessToken(accessToken)
                    .profileId(addProfile.getProfileId())
                    .build();
            return ResponseEntity.status(HttpStatus.CREATED).body(addProfileResponseDto);
        } catch (IOException e) {

            throw new IllegalArgumentException(e);
        }
    }

    @Transactional
    @Override
    public List<ProfileListResponseDto> profileList(String email) {
        Member member = getMember(email);
        List<Profile> lists = profileRepository.findByMemberId(member.getMemberId());
        List<ProfileListResponseDto> profileListResponseDtos = new ArrayList<>();
        for (Profile list : lists) {
            ProfileListResponseDto profileListResponseDto = ProfileListResponseDto.builder()
                    .profileId(list.getProfileId())
                    .imageUrl(list.getImageUrl())
                    .dogName(list.getDogName())
                    .hasBirthday(false)
                    .build();
            if (MonthDay.from(LocalDate.now()).equals(MonthDay.from(list.getDogBirthDate()))) {
                profileListResponseDto.setHasBirthday(true);
            }
            profileListResponseDtos.add(profileListResponseDto);
        }
        return profileListResponseDtos;
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
                    .dogName(profile.get().getDogName())
                    .dogAge(profile.get().getDogAge())
                    .dogBirthDate(profile.get().getDogBirthDate())
                    .avoidBreeds(profile.get().getAvoidBreeds())
                    .isOwner(false)
                    .build();

            if (member.getMemberId() == profile.get().getMemberId()) {
                getProfileResponseDto.setOwner(true);
            }
            return ResponseEntity.ok().body(getProfileResponseDto);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("해당 프로필은 없습니다.");
        }
    }

    @Transactional
    @Override
    public ResponseEntity updateProfile(Long profileId, ProfileDto profileDto, String email) {
        Member member = getMember(email);
        Optional<Profile> profile = profileRepository.findById(profileId);
        Optional<DogBreed> dogBreed = dogBreedService.findByName(profileDto.getDogBreed());
        if (dogBreed.isEmpty()) {
            return ResponseEntity.badRequest().body("종을 다시 입력해주세요.");
        }
        if (profile.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("해당 프로필은 없습니다.");
        } else {
            if (member.getMemberId() == profile.get().getMemberId()) {
                Profile newProfile = profile.get();
                UUID uuid = UUID.randomUUID();
                String imageFileName = uuid + "_" + profileDto.getProfileImageFile().getOriginalFilename();

                newProfile.setImageUrl("/profile/" + imageFileName);
                newProfile.setDogName(profileDto.getDogName());
                newProfile.setDogBirthDate(profileDto.getDogBirthDate());
                newProfile.setDogAge(CalculateAge(profileDto.getDogBirthDate()) + "살");
                newProfile.setDogBreed(profileDto.getDogBreed());
                newProfile.setExtraInfo(profileDto.getExtraInfo());
                newProfile.getAvoidBreeds().clear();
                String[] arr = profileDto.getAvoidBreeds().split(",");
                for (String breeds : arr) {
                    Optional<DogBreed> avoidBreed = dogBreedService.findByName(breeds);
                    if (avoidBreed.isEmpty()) {
                        return ResponseEntity.badRequest().body("피해야하는 종을 다시 입력해주세요.");
                    }
                    newProfile.addAvoidBreeds(avoidBreed.get());
                }
                return ResponseEntity.ok().body(profile);
            }else {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("수정 권한이 없습니다.");
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
    public ResponseEntity<?> accessTokenToProfileId(Long profileId, String email) {
        Optional<Profile> profile = profileRepository.findById(profileId);
        Member member = memberRepository.findByEmail(email).get();
        if (profile.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("해당 프로필은 없습니다.");
        } else if (!(profile.get().getMemberId().equals(member.getMemberId()))) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("권한이 없습니다.");
        }
        List<String> roles = member
                .getRoles()
                .stream()
                .map(Role::getName)
                .collect(Collectors.toList());

        String accessToken = jwtTokenizer.createAccessToken(member.getMemberId(), profileId, member.getEmail(), roles);
        AccessTokenToProfileIdResponseDto accessTokenToProfileIdResponseDto = AccessTokenToProfileIdResponseDto.builder()
                .profileId(profileId)
                .accessToken(accessToken)
                .build();
        return ResponseEntity.status(HttpStatus.CREATED).body(accessTokenToProfileIdResponseDto);
    }

    @Transactional
    @Override
    public Long getCount(String email) {
        Member member = getMember(email);
        return profileRepository.countByMemberId(member.getMemberId());
    }

    private Member getMember(String email) {
        return memberRepository.findByEmail(email).get();//수정해야할듯?
    }

    private int CalculateAge(LocalDate dogBirthDate) {
            return Period.between(dogBirthDate, LocalDate.now()).getYears();
    }

}
