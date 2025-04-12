package com.example.PetApp.config.stomp;


import com.example.PetApp.domain.ChatRoom;
import com.example.PetApp.domain.Profile;
import com.example.PetApp.repository.ChatRoomRepository;
import com.example.PetApp.repository.ProfileRepository;
import com.example.PetApp.security.jwt.token.JwtAuthenticationToken;
import com.example.PetApp.security.jwt.util.JwtTokenizer;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;

import java.security.Principal;
import java.util.Optional;

@RequiredArgsConstructor
@Configuration//jwt토큰인증 해야할듯
@Slf4j
public class StompHandler implements ChannelInterceptor {

    private final JwtTokenizer jwtTokenizer;
    private final ChatRoomRepository chatRoomRepository;
    private final ProfileRepository profileRepository;


    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
        if (StompCommand.CONNECT.equals(accessor.getCommand())) {//connect로 들어온 요청은 jwt토큰 인증을 해야됨.
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

            try {
                Claims claims = jwtTokenizer.parseAccessToken(accessToken);
                Object profileId = claims.get("profileId");
                JwtAuthenticationToken jwtAuthenticationToken = new JwtAuthenticationToken(null, null, null, profileId);
                //인증 객체를 만들어서 accessor.setUser에 전달.
                accessor.setUser(jwtAuthenticationToken);


            } catch (Exception e) {
                log.error("websocket 인증 에러: 토큰 파싱 실패", e);
                throw new IllegalArgumentException("유효하지 않은 토큰입니다.");
            }

        } else if (StompCommand.SUBSCRIBE.equals(accessor.getCommand())) {//목적지 주소와 같은지 확인을 해야됨.
            String destination = accessor.getDestination();
            if (destination != null && destination.startsWith("/sub/chat/")) {//null이면 안되고 /sub/chat/으로 시작을 해야됨.
                Long chatRoomId = Long.valueOf(destination.substring("/sub/chat/".length()));//이거는 chatRoomId가 나옴. chatRoom에 있는 profileId와 검사를 해야됨.
                JwtAuthenticationToken token = (JwtAuthenticationToken) accessor.getUser();//accessor의 principal을 jwtAuthenticaionToken으로 형변환.
                Long profileId = token.getProfileId();
                ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId).orElseThrow(() -> new IllegalArgumentException("채팅방이 존재하지 앖습니다."));
                Profile profile = profileRepository.findById(profileId).orElseThrow(() -> new IllegalArgumentException("프로필이 존재하지 않습니다."));
                boolean result = chatRoomRepository.existsByChatRoomIdAndProfilesContains(chatRoomId, profile);
                if (!result) {
                    log.error("chatRoomId:{} 에 권한이 없는 profile이 접근하려고함.",chatRoomId);
                    throw new IllegalArgumentException("잘못된 접근입니다.");
                }
            }

        }


        return message;
    }
}
