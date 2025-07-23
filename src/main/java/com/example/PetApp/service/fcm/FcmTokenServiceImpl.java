package com.example.PetApp.service.fcm;

import com.example.PetApp.domain.FcmToken;
import com.example.PetApp.domain.Member;
import com.example.PetApp.repository.jpa.FcmTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FcmTokenServiceImpl implements FcmTokenService {

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
