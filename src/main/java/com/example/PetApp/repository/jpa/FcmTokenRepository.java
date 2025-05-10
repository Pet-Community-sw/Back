package com.example.PetApp.repository.jpa;

import com.example.PetApp.domain.FcmToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FcmTokenRepository extends JpaRepository<FcmToken, Long> {

}
