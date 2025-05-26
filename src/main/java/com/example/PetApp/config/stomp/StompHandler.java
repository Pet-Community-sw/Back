package com.example.PetApp.config.stomp;

import com.example.PetApp.domain.*;
import com.example.PetApp.repository.jpa.*;
import com.example.PetApp.security.jwt.util.JwtTokenizer;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import java.security.Principal;

@RequiredArgsConstructor
@Configuration
@Slf4j
public class StompHandler implements ChannelInterceptor {

    private final ChatRoomRepository chatRoomRepository;
    private final ProfileRepository profileRepository;
    private final MemberChatRoomRepository memberChatRoomRepository;
    private final MemberRepository memberRepository;
    private final WalkRecordRepository walkRecordRepository;
    private final ObjectMapper objectMapper;
    private final StringRedisTemplate stringRedisTemplate;
    private final JwtTokenizer jwtTokenizer;



    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);//매번 새로운 accessor을 생성
        //현재처리중인 stomp메시지 헤더를 가지고오는거임.
        log.info(" interceptor 진입 - accessor: {}", accessor);
        if (StompCommand.CONNECT.equals(accessor.getCommand())) {//connect로 들어온 요청은 jwt토큰 인증을 해야됨. setuser을 해도 sub까지 유지가 안됨.
            //토큰 값이 유효 한지만 확인.
            String token = accessor.getFirstNativeHeader("Authorization");
            if (token == null) {
                log.error("websocket 인증 에러 : null");
                throw new NullPointerException("비어있는 토큰");
            }
            String[] arr = token.split(" ");
            String accessToken = arr[1];
            if (jwtTokenizer.isTokenExpired("access", accessToken)) {
                log.error("websocket 인증 에러 : expired");
                throw new IllegalArgumentException("만료된 토큰");
            }
            Claims claims = jwtTokenizer.parseAccessToken(accessToken);
            Object profileId = claims.get("profileId");
            Authentication authentication;
            if (profileId == null) {
                String email = claims.getSubject();
                Member member = memberRepository.findByEmail(email)
                        .orElseThrow(() -> new RuntimeException("해당 이메일의 사용자를 찾을 수 없습니다."));
                authentication = new UsernamePasswordAuthenticationToken(member.getMemberId(), null);
                log.info("websocket 인증: profileId 없음 → memberId({}) 사용", member.getMemberId());
            } else {
                authentication = new UsernamePasswordAuthenticationToken(profileId, null);
                log.info("websocket 인증: profileId({}) 사용", profileId);
            }
            accessor.setUser(authentication);
        } else if (StompCommand.SUBSCRIBE.equals(accessor.getCommand())) {//목적지 주소와 같은지 확인을 해야됨. 구독을 검증하는 단계
            String destination = accessor.getDestination();
            String subscriptionId = accessor.getSubscriptionId();
            if (destination != null && destination.startsWith("/sub/chat/")) {//null이면 안되고 /sub/chat/으로 시작을 해야됨.
                Long chatRoomId = Long.valueOf(destination.substring("/sub/chat/".length()));//이거는 chatRoomId가 나옴. chatRoom에 있는 profileId와 검사를 해야됨.
                chatRoomRepository.findById(chatRoomId).orElseThrow(() -> new IllegalArgumentException("채팅방이 존재하지 앖습니다."));
                Principal auth = accessor.getUser();
                if (auth == null) {
                    throw new NullPointerException("auth가 null입니다.");
                }
                String profileId = auth.getName();
                Profile profile = profileRepository.findById(Long.valueOf(profileId)).orElseThrow(() -> new IllegalArgumentException("프로필이 존재하지 않습니다."));
                boolean result = chatRoomRepository.existsByChatRoomIdAndProfilesContains(chatRoomId, profile);
                if (!result) {
                    log.error("chatRoomId:{} 에 권한이 없는 profile이 접근하려고함.", chatRoomId);
                    throw new IllegalArgumentException("잘못된 접근입니다.");
                }
                stringRedisTemplate.opsForSet().add("chatRoomId:" + chatRoomId + ":onlineProfiles", profileId);//채팅방 접속자 유무
                String sessionId = accessor.getSessionId();
                stringRedisTemplate.opsForValue().set("session:"+sessionId, chatRoomId.toString());//chatRoomId를 가지고오기 위한 redis 저장
                SubscribeInfo subscribeInfo = new SubscribeInfo(chatRoomId, profileId, ChatMessage.ChatRoomType.MANY);
                try {
                    String json = objectMapper.writeValueAsString(subscribeInfo);
                    stringRedisTemplate.opsForValue().set("subscriptionId:"+subscriptionId, json);//unsubscribe를 위한 정보 저장
                } catch (JsonProcessingException e) {
                    throw new RuntimeException("subscribeInfo string 변환 과정 중 에러 발생",e);
                }
            } else if (destination.startsWith("/sub/member/chat/")) {
                    // 1:1 채팅방 처리
                Long memberChatRoomId = Long.valueOf(destination.substring("/sub/member/chat/".length()));
                MemberChatRoom memberChatRoom = memberChatRoomRepository.findById(memberChatRoomId)
                            .orElseThrow(() -> new IllegalArgumentException("1:1 채팅방이 존재하지 않습니다."));
                String memberId = accessor.getUser().getName();

                boolean hasAccess = memberChatRoom.getMembers().stream()
                        .anyMatch(m -> m.getMemberId().equals(Long.valueOf(memberId)));
                if (!hasAccess) {
                    log.error("memberChatRoomId:{} 에 권한이 없는 member가 접근하려고 함.", memberChatRoomId);
                    throw new IllegalArgumentException("잘못된 접근입니다.");
                }
                stringRedisTemplate.opsForSet().add("memberChatRoomId:" + memberChatRoomId + ":onlineMembers", memberId);
                String sessionId = accessor.getSessionId();
                stringRedisTemplate.opsForValue().set("session:" + sessionId, memberChatRoomId.toString());
                SubscribeInfo subscribeInfo = new SubscribeInfo(memberChatRoomId, memberId, ChatMessage.ChatRoomType.ONE);
                try {
                    String json = objectMapper.writeValueAsString(subscribeInfo);
                    stringRedisTemplate.opsForValue().set("subscriptionId:"+subscriptionId, json);//unsubscribe를 위한 정보 저장
                } catch (JsonProcessingException e) {
                    throw new RuntimeException("subscribeInfo string 변환 과정 중 에러 발생", e);
                }
            } else if (destination.startsWith("/sub/walk-record/location/")) {//unsubscribe 필요 없을듯?
                Long walkRecordId = Long.valueOf(destination.substring("/sub/walk-record/location/".length()));
                WalkRecord walkRecord = walkRecordRepository.findById(walkRecordId)
                        .orElseThrow(() -> new IllegalArgumentException("산책기록이 존재하지 않습니다."));
                String memberId = accessor.getUser().getName();
                if (!(walkRecord.getDelegateWalkPost().getProfile().getMember().getMemberId().equals(Long.valueOf(memberId)))) {
                    log.error("walkRecordId : {} 에 권한이 없는 member가 접근하려고 함.", walkRecord.getWalkRecordId());
                    throw new IllegalArgumentException("잘못된 접근입니다.");
                }
            } else {
                log.error("알 수 없는 구독 경로: {}", destination);
                throw new IllegalArgumentException("알 수 없는 구독 경로입니다.");
            }
        } else if (StompCommand.UNSUBSCRIBE.equals(accessor.getCommand())) {
            String subscriptionId = accessor.getSubscriptionId();
            if (subscriptionId == null) {
                log.error("UNSUBSCRIBE 요청 오류 - subscriptionId 없음");
                throw new IllegalArgumentException("subscriptionId 없음.");
            }

            log.info("UNSUBSCRIBE 요청 - subscriptionId: {}", subscriptionId);

            String json = stringRedisTemplate.opsForValue().get("subscriptionId:" + subscriptionId);
            try {
                SubscribeInfo subscribeInfo = objectMapper.readValue(json, SubscribeInfo.class);

                if (subscribeInfo.getChatRoomType() == ChatMessage.ChatRoomType.MANY) {
                    stringRedisTemplate.opsForSet().remove(
                            "chatRoomId:" + subscribeInfo.getChatRoomId() + ":onlineMembers",
                            subscribeInfo.getUserId()
                    );
                } else {
                    stringRedisTemplate.opsForSet().remove(
                            "memberChatRoomId:" + subscribeInfo.getChatRoomId() + ":onlineMembers",
                            subscribeInfo.getUserId()
                    );
                }
                stringRedisTemplate.opsForSet().remove("subscriptionId" + subscriptionId);

            } catch (JsonProcessingException e) {
                log.error("UNSUBSCRIBE 처리 중 JSON 파싱 에러", e);
                throw new RuntimeException("string을 subscribeInfo.class로 변환 중 에러 발생", e);
            }
        }

        return message;
    }
}
