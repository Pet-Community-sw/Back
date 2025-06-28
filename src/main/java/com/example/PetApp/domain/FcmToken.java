package com.example.PetApp.domain;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.time.LocalDateTime;

@Getter
@Entity
@Table(name = "fcm_token")
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FcmToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long fcmTokenId;

    @Setter
    @NotBlank
    @Column(nullable = false)
    private String fcmToken;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime fcmTokenTime;
}
