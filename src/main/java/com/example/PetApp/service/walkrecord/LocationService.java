package com.example.PetApp.service.walkrecord;

import com.example.PetApp.dto.walkrecord.LocationMessage;
import org.springframework.stereotype.Repository;

@Repository
public interface LocationService {
    void sendLocation(LocationMessage locationMessage, String memberId);
}
