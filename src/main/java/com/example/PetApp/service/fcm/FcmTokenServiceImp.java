package com.example.PetApp.service.fcm;

import com.example.PetApp.domain.FcmToken;
import com.example.PetApp.domain.Member;
import com.example.PetApp.dto.member.FcmTokenDto;
import com.example.PetApp.repository.jpa.FcmTokenRepository;
import com.example.PetApp.repository.jpa.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FcmTokenServiceImp implements FcmTokenService {

    private final FcmTokenRepository fcmTokenRepository;
    private final MemberRepository memberRepository;

    @Override
    public ResponseEntity<?> createFcmToken(FcmTokenDto fcmTokenDto) {
        Optional<Member> member = memberRepository.findById(fcmTokenDto.getMemberId());
        if (member.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("해당 유저는 없습니다.");
        }
        FcmToken fcmToken=FcmToken.builder()
                .member(member.get())
                .fcmToken(fcmTokenDto.getFcmToken())
                .build();
        fcmTokenRepository.save(fcmToken);
        return ResponseEntity.status(HttpStatus.CREATED).body("생성 완료.");
    }

    //업데이트 로직.

}
