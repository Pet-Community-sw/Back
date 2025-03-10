package com.example.PetApp.domain;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "refresh_token")
@Getter
@Setter
@NoArgsConstructor
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long memberId;

    private String value;

    @Override
    public String toString() {
        return "RefreshToken{" +
                "id=" + id +
                ", memberId=" + memberId +
                ", value='" + value + '\'' +
                '}';
    }
}
