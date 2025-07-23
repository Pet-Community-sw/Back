package com.example.PetApp.controller;

import com.example.PetApp.dto.walkrecord.LocationMessage;
import com.example.PetApp.service.walkrecord.LocationService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@Controller
@RequiredArgsConstructor//서버에서 클라이언트에게만 보냄으로 sse로 해야됨.
public class LocationController {
    private final LocationService locationService;

    @MessageMapping("/location/send")
    public void sendLocation(LocationMessage locationMessage, Principal principal) {
        String memberId = principal.getName();
        locationService.sendLocation(locationMessage, memberId);
    }
}
