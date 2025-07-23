package com.example.PetApp.service;

import com.example.PetApp.domain.DelegateWalkPost;
import com.example.PetApp.domain.Member;
import com.example.PetApp.domain.Profile;
import com.example.PetApp.domain.WalkRecord;
import com.example.PetApp.dto.location.SendLocationDto;
import com.example.PetApp.dto.walkrecord.LocationMessage;
import com.example.PetApp.exception.ForbiddenException;
import com.example.PetApp.exception.NotFoundException;
import com.example.PetApp.mapper.LocationMapper;
import com.example.PetApp.repository.jpa.WalkRecordRepository;
import com.example.PetApp.service.walkrecord.LocationServiceImpl;
import com.example.PetApp.util.HaversineUtil;
import com.example.PetApp.util.SendNotificationUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class LocationServiceTest {
    @InjectMocks
    private LocationServiceImpl locationServiceImpl;
    @Mock
    private SimpMessagingTemplate simpMessagingTemplate;
    @Mock
    private StringRedisTemplate stringRedisTemplate;
    @Mock
    private WalkRecordRepository walkRecordRepository;
    @Mock
    private SendNotificationUtil sendNotificationUtil;
    @Mock
    private ListOperations<String, String> listOperations;

    @Test
    @DisplayName("sendLocation_성공")
    void test1() {
        // given
        Long walkRecordId = 1L;
        Long memberId = 2L;
        LocationMessage message = LocationMessage.builder()
                .walkRecordId(walkRecordId)
                .longitude(127.0)
                .latitude(37.0)
                .build();

        Member walker = Member.builder().memberId(memberId).name("초이선자이").build();
        Member owner = Member.builder().memberId(200L).name("작성자").build();

        Profile profile = Profile.builder().member(owner).build();

        DelegateWalkPost delegateWalkPost = DelegateWalkPost.builder()
                .selectedApplicantMemberId(memberId)
                .allowedRadiusMeters(10)
                .profile(profile)
                .build();

        WalkRecord walkRecord = WalkRecord.builder()
                .walkRecordId(walkRecordId)
                .walkStatus(WalkRecord.WalkStatus.START)
                .member(walker)
                .delegateWalkPost(delegateWalkPost)
                .build();

        SendLocationDto sendLocationDto = SendLocationDto.builder()
                .walkerLatitude(37.0005)
                .walkerLongitude(127.0005)
                .locationLatitude(37.0)
                .locationLongitude(127.0)
                .build();

        when(walkRecordRepository.findById(walkRecordId)).thenReturn(Optional.of(walkRecord));
        when(stringRedisTemplate.opsForList()).thenReturn(listOperations);


        try (
                MockedStatic<LocationMapper> locationMapperStatic = mockStatic(LocationMapper.class);
                MockedStatic<HaversineUtil> haversineUtilStatic = mockStatic(HaversineUtil.class)
        ) {
            locationMapperStatic.when(() -> LocationMapper.toSendLocationDto(walkRecord, message)).thenReturn(sendLocationDto);

            haversineUtilStatic.when(() -> HaversineUtil.calculateDistanceInMeters(37.0, 127.0, 37.0005, 127.0005)).thenReturn(15.0);

            // when
            locationServiceImpl.sendLocation(message, memberId.toString());

            // then
            verify(sendNotificationUtil).sendNotification(eq(owner), contains("벗어났습니다"));
            verify(sendNotificationUtil).sendNotification(eq(walker), contains("산책 범위에 들어가주세요"));
            verify(listOperations).rightPush(eq("walk:path:" + walkRecordId), anyString());
            verify(simpMessagingTemplate).convertAndSend(eq("/sub/walk-record/location/" + walkRecordId), eq(message));
        }
    }

    @Test
    @DisplayName("범위를 벗어났을 때 보호자와 산책자에게 알림 전송")
    void test2() {
        // given
        Long walkRecordId = 1L;
        String memberId = "100";
        double baseLat = 37.0, baseLon = 127.0;
        double walkerLat = 37.001, walkerLon = 127.001;

        Member walker = Member.builder().memberId(100L).name("산책자").build();
        Member owner = Member.builder().memberId(200L).name("보호자").build();
        Profile profile = Profile.builder().member(owner).build();

        DelegateWalkPost post = DelegateWalkPost.builder()
                .selectedApplicantMemberId(100L)
                .allowedRadiusMeters(50)
                .profile(profile)
                .build();

        WalkRecord walkRecord = WalkRecord.builder()
                .walkRecordId(walkRecordId)
                .walkStatus(WalkRecord.WalkStatus.START)
                .delegateWalkPost(post)
                .member(walker)
                .build();

        LocationMessage message = LocationMessage.builder()
                .walkRecordId(walkRecordId)
                .longitude(baseLon)
                .latitude(baseLat)
                .build();

        SendLocationDto sendLocationDto = SendLocationDto.builder()
                .walkerLatitude(walkerLat)
                .walkerLongitude(walkerLon)
                .locationLatitude(baseLat)
                .locationLongitude(baseLon)
                .build();

        when(walkRecordRepository.findById(walkRecordId)).thenReturn(Optional.of(walkRecord));
        when(stringRedisTemplate.opsForList()).thenReturn(listOperations);

        try (
                MockedStatic<LocationMapper> locationMapperStatic = mockStatic(LocationMapper.class);
                MockedStatic<HaversineUtil> haversineUtilStatic = mockStatic(HaversineUtil.class)
        ) {
            locationMapperStatic.when(() -> LocationMapper.toSendLocationDto(walkRecord, message))
                    .thenReturn(sendLocationDto);

            haversineUtilStatic.when(() -> HaversineUtil.calculateDistanceInMeters(baseLat, baseLon, walkerLat, walkerLon)).thenReturn(60.0);

            // when
            locationServiceImpl.sendLocation(message, memberId);

            // then
            verify(sendNotificationUtil).sendNotification(eq(owner), contains("벗어났습니다"));
            verify(sendNotificationUtil).sendNotification(eq(walker), contains("산책 범위에 들어가주세요"));
            verify(listOperations).rightPush(eq("walk:path:" + walkRecordId), anyString());
            verify(simpMessagingTemplate).convertAndSend(eq("/sub/walk-record/location/" + walkRecordId), eq(message));
        }
    }

    @Test
    @DisplayName("sendLocation_산책 기록이 없는 경우_실패")
    void test3() {
        // given
        Long walkRecordId = 1L;
        LocationMessage locationMessage = LocationMessage.builder()
                .walkRecordId(walkRecordId)
                .build();

        when(walkRecordRepository.findById(walkRecordId)).thenReturn(Optional.empty());

        // expect
        assertThatThrownBy(() -> locationServiceImpl.sendLocation(locationMessage, anyString()))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("해당 산책 기록이 없습니다.");
    }

    @Test
    @DisplayName("sendLocation_권한이 없는 경우_실패")
    void test4() {
        // given
        String memberId = "2";

        LocationMessage locationMessage = LocationMessage.builder()
                .walkRecordId(1L)
                .build();

        DelegateWalkPost delegateWalkPost = DelegateWalkPost.builder()
                .selectedApplicantMemberId(1L)
                .build();

        WalkRecord walkRecord=WalkRecord.builder()
                .delegateWalkPost(delegateWalkPost)
                .build();

        when(walkRecordRepository.findById(locationMessage.getWalkRecordId())).thenReturn(Optional.of(walkRecord));

        //when & then
        assertThatThrownBy(() -> locationServiceImpl.sendLocation(locationMessage, memberId))
                .isInstanceOf(ForbiddenException.class)
                .hasMessage("접근 권한 없음.");
    }

    @Test
    @DisplayName("sendLocation_start 권한이 없는 경우_실패")
    void test5() {
        // given
        Long walkRecordId = 1L;
        String memberId = "100";

        Member walker = Member.builder().memberId(100L).build();
        Member owner = Member.builder().memberId(200L).build();
        Profile profile = Profile.builder().member(owner).build();
        DelegateWalkPost post = DelegateWalkPost.builder()
                .selectedApplicantMemberId(100L)
                .profile(profile)
                .build();

        WalkRecord walkRecord = WalkRecord.builder()
                .walkRecordId(walkRecordId)
                .walkStatus(WalkRecord.WalkStatus.READY)
                .delegateWalkPost(post)
                .member(walker)
                .build();

        LocationMessage message = LocationMessage.builder()
                .walkRecordId(walkRecordId)
                .build();

        when(walkRecordRepository.findById(walkRecordId)).thenReturn(Optional.of(walkRecord));

        // when & then
        assertThatThrownBy(() -> locationServiceImpl.sendLocation(message, memberId))
                .isInstanceOf(ForbiddenException.class)
                .hasMessage("start 권한 없음.");
    }
}
