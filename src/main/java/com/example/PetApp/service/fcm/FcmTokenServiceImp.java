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
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FcmTokenServiceImp implements FcmTokenService {

    private final FcmTokenRepository fcmTokenRepository;

    @Override
    public void createFcmToken(Member member, String token) {
        FcmToken fcmToken=FcmToken.builder()
                .member(member)
                .fcmToken(token)
                .build();
        fcmTokenRepository.save(fcmToken);
    }

    //업데이트 로직.

}
