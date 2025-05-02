package com.example.PetApp.service.profile;

import com.example.PetApp.domain.*;
import com.example.PetApp.dto.profile.*;
import com.example.PetApp.repository.jpa.MemberRepository;
import com.example.PetApp.repository.jpa.ProfileRepository;
import com.example.PetApp.security.jwt.util.JwtTokenizer;
import com.example.PetApp.service.dogBreed.PetBreedService;
import com.example.PetApp.util.RedisUtil;
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
    private final PetBreedService petBreedService;
    private final JwtTokenizer jwtTokenizer;
    private final RedisUtil redisUtil;

    @Override
    @Transactional//accesstoken 수정 필요 이름이 같은지 확인해야됨.
    public ResponseEntity addProfile(ProfileDto profileDto, String email) {
        Member member = getMember(email);

        MultipartFile file = profileDto.getPetImageUrl();

        UUID uuid = UUID.randomUUID();
        String imageFileName = uuid + "_" + file.getOriginalFilename();

        Optional<PetBreed> dogBreed = petBreedService.findByName(profileDto.getPetBreed());
        if (dogBreed.isEmpty()) {
            return ResponseEntity.badRequest().body("종을 다시 입력해주세요.");
        }
        try {
            Path path = Paths.get(profileUploadDir, imageFileName);
            Files.copy(file.getInputStream(), path);

            Profile profile = Profile.builder()
                    .member(member)
                    .petImageUrl("/profile/" + imageFileName)
                    .petBirthDate(profileDto.getPetBirthDate())
                    .extraInfo(profileDto.getExtraInfo())
                    .petBreed(profileDto.getPetBreed())
                    .petAge(CalculateAge(profileDto.getPetBirthDate())+"살")
                    .petName(profileDto.getPetName())
                    .build();
            String[] arr = profileDto.getAvoidBreeds().split(",");
            for (String breeds : arr) {
                breeds=breeds.trim();
                Optional<PetBreed> avoidBreed = petBreedService.findByName(breeds);
                if (avoidBreed.isEmpty()) {
                    return ResponseEntity.badRequest().body("피해야하는 종을 다시 입력해주세요.");
                }
                if (profile.getAvoidBreeds() == null) {
                    profile.setAvoidBreeds(new HashSet<>());
                }
                profile.addAvoidBreeds(avoidBreed.get());
            }
            Profile addProfile = profileRepository.save(profile);
//            List<String> roles = member.getRoles().stream().map(Role::getName).collect(Collectors.toList());
//
//            String accessToken = jwtTokenizer.createAccessToken(member.getMemberId(), addProfile.getProfileId(), member.getEmail(), roles);//accessToken 추가
//            //refresh는 노.
//            AddProfileResponseDto addProfileResponseDto=AddProfileResponseDto.builder()//profileId넣어서 토큰 반환.
//                    .accessToken(accessToken)
//                    .profileId(addProfile.getProfileId())
//                    .build();
            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("profileId",addProfile.getProfileId()));
        } catch (IOException e) {

            throw new IllegalArgumentException(e);
        }
    }

    @Transactional
    @Override
    public List<ProfileListResponseDto> profileList(String email) {
        Member member = getMember(email);
        List<Profile> lists = profileRepository.findByMemberMemberId(member.getMemberId());
        List<ProfileListResponseDto> profileListResponseDtos = new ArrayList<>();
        for (Profile list : lists) {
            ProfileListResponseDto profileListResponseDto = ProfileListResponseDto.builder()
                    .profileId(list.getProfileId())
                    .petImageUrl(list.getPetImageUrl())
                    .petName(list.getPetName())
                    .hasBirthday(false)
                    .build();
            if (MonthDay.from(LocalDate.now()).equals(MonthDay.from(list.getPetBirthDate()))) {
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
                    .petBreed(profile.get().getPetBreed())
                    .petImageUrl(profile.get().getPetImageUrl())
                    .memberId(profile.get().getMember().getMemberId())
                    .petName(profile.get().getPetName())
                    .petAge(profile.get().getPetAge())
                    .petBirthDate(profile.get().getPetBirthDate())
                    .avoidBreeds(profile.get().getAvoidBreeds())
                    .isOwner(false)
                    .build();

            if (member.equals(profile.get().getMember())) {
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
        Optional<PetBreed> dogBreed = petBreedService.findByName(profileDto.getPetBreed());
        if (dogBreed.isEmpty()) {
            return ResponseEntity.badRequest().body("종을 다시 입력해주세요.");
        }
        if (profile.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("해당 프로필은 없습니다.");
        } else {
            if (member.equals(profile.get().getMember())) {
                Profile newProfile = profile.get();
                UUID uuid = UUID.randomUUID();
                String imageFileName = uuid + "_" + profileDto.getPetImageUrl().getOriginalFilename();

                newProfile.setPetImageUrl("/profile/" + imageFileName);
                newProfile.setPetName(profileDto.getPetName());
                newProfile.setPetBirthDate(profileDto.getPetBirthDate());
                newProfile.setPetAge(CalculateAge(profileDto.getPetBirthDate()) + "살");
                newProfile.setPetBreed(profileDto.getPetBreed());
                newProfile.setExtraInfo(profileDto.getExtraInfo());
                newProfile.getAvoidBreeds().clear();
                String[] arr = profileDto.getAvoidBreeds().split(",");
                for (String breeds : arr) {
                    Optional<PetBreed> avoidBreed = petBreedService.findByName(breeds);
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
            if (member.equals(profile.get().getMember())) {
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
    public ResponseEntity<?> accessTokenToProfileId(String accessToken, Long profileId, String email) {//요청했을 당시 토큰을 redis에 저장시켜서 이전 토큰으로 요청 시 인증이 안되게 끔 해야됨.

        Optional<Profile> profile = profileRepository.findById(profileId);
        Member member = memberRepository.findByEmail(email).get();
        if (profile.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("해당 프로필은 없습니다.");
        } else if (!(profile.get().getMember().equals(member))) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("권한이 없습니다.");
        }
        List<String> roles = member
                .getRoles()
                .stream()
                .map(Role::getName)
                .collect(Collectors.toList());

        String accessToken1 = jwtTokenizer.createAccessToken(member.getMemberId(), profileId, member.getEmail(), roles);
        redisUtil.createData(accessToken, "blacklist", 30 * 60L);//에세스토큰 유효시간
        AccessTokenToProfileIdResponseDto accessTokenToProfileIdResponseDto = AccessTokenToProfileIdResponseDto.builder()
                .profileId(profileId)
                .accessToken(accessToken1)
                .build();
        return ResponseEntity.status(HttpStatus.CREATED).body(accessTokenToProfileIdResponseDto);
    }

    @Transactional
    @Override
    public Long getCount(String email) {
        Member member = getMember(email);
        return profileRepository.countByMember(member);
    }

    private Member getMember(String email) {
        return memberRepository.findByEmail(email).get();//수정해야할듯?
    }

    private int CalculateAge(LocalDate dogBirthDate) {
            return Period.between(dogBirthDate, LocalDate.now()).getYears();
    }

}
