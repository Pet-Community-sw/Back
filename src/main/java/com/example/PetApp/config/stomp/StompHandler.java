package com.example.PetApp.config.stomp;

import com.example.PetApp.domain.ChatRoom;
import com.example.PetApp.domain.Member;
import com.example.PetApp.domain.MemberChatRoom;
import com.example.PetApp.domain.Profile;
import com.example.PetApp.repository.jpa.ChatRoomRepository;
import com.example.PetApp.repository.jpa.MemberChatRoomRepository;
import com.example.PetApp.repository.jpa.MemberRepository;
import com.example.PetApp.repository.jpa.ProfileRepository;
import com.example.PetApp.security.jwt.token.JwtAuthenticationToken;
import com.example.PetApp.security.jwt.util.JwtTokenizer;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import java.nio.file.attribute.UserPrincipal;
import java.security.Principal;

@RequiredArgsConstructor
@Configuration//jwt토큰인증 해야할듯
@Slf4j
public class StompHandler implements ChannelInterceptor {

    private final JwtTokenizer jwtTokenizer;
    private final ChatRoomRepository chatRoomRepository;
    private final ProfileRepository profileRepository;
    private final MemberRepository memberRepository;
    private final MemberChatRoomRepository memberChatRoomRepository;


    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);//매번 새로운 accessor을 생성 x
        log.info("🔥 interceptor 진입 - accessor: {}", accessor);
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
            if (profileId == null) {
                String email = claims.getSubject();
                Member member = memberRepository.findByEmail(email).get();
                Authentication authentication = new UsernamePasswordAuthenticationToken(member.getMemberId(), null);
                accessor.setUser(authentication);//memberId를 추가
            }
            Authentication authentication = new UsernamePasswordAuthenticationToken(profileId, null);//profileId를 넣고있음.
            accessor.setUser(authentication);
        } else if (StompCommand.SUBSCRIBE.equals(accessor.getCommand())) {//목적지 주소와 같은지 확인을 해야됨. 구독을 검증하는 단계
            String destination = accessor.getDestination();
            if (destination != null && destination.startsWith("/sub/chat/")) {//null이면 안되고 /sub/chat/으로 시작을 해야됨.
                Long chatRoomId = Long.valueOf(destination.substring("/sub/chat/".length()));//이거는 chatRoomId가 나옴. chatRoom에 있는 profileId와 검사를 해야됨.
                ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId).orElseThrow(() -> new IllegalArgumentException("채팅방이 존재하지 앖습니다."));
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
                } else {
                    log.error("알 수 없는 구독 경로: {}", destination);
                    throw new IllegalArgumentException("알 수 없는 구독 경로입니다.");
                }
        }
        return message;
    }
}
