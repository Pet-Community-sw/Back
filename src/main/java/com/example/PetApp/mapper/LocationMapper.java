package com.example.PetApp.mapper;

import com.example.PetApp.domain.WalkRecord;
import com.example.PetApp.dto.location.SendLocationDto;
import com.example.PetApp.dto.walkrecord.LocationMessage;

public class LocationMapper {

    public static SendLocationDto toSendLocationDto(WalkRecord walkRecord, LocationMessage locationMessage) {
        return SendLocationDto.builder()
                .locationLongitude(walkRecord.getDelegateWalkPost().getLocationLongitude())
                .locationLatitude(walkRecord.getDelegateWalkPost().getLocationLatitude())
                .walkerLongitude(locationMessage.getLongitude())
                .walkerLatitude(locationMessage.getLatitude())
                .build();

    }
}
