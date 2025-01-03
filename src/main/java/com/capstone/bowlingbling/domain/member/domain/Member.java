package com.capstone.bowlingbling.domain.member.domain;

import com.capstone.bowlingbling.domain.club.domain.Club;
import com.capstone.bowlingbling.domain.club.domain.ClubJoinList;
import com.capstone.bowlingbling.domain.lessoninfo.domain.LikedLesson;
import com.capstone.bowlingbling.global.BaseEntity;
import com.capstone.bowlingbling.global.enums.ClubRole;
import com.capstone.bowlingbling.global.enums.Role;
import com.capstone.bowlingbling.global.enums.SocialType;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

@Entity
@Builder(toBuilder = true)
@Table(name = "members")
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Member extends BaseEntity {

    @Column(nullable = false, unique = true)
    private String email; // 이메일
    private String name;
    private String nickname; // 닉네임
    private String password; // 비밀번호
    private String city; // 사는 도시
    private Integer age;
    private String phonenum;
    private String image; // 프로필 이미지
    private String introduction;
    private String sex;
    private Integer myaver;

    @Enumerated(EnumType.STRING)
    private Role role;

    @Enumerated(EnumType.STRING)
    private SocialType socialType; // KAKAO, NAVER, GOOGLE

    private String socialId; // 로그인한 소셜 타입의 식별자 값 (일반 로그인인 경우 null)

    private String refreshToken; // 리프레시 토큰

    @OneToMany
    private List<ClubJoinList> clubJoinList;

    @OneToMany(mappedBy = "member")
    private List<LikedLesson> likedLessons;

    // 유저 권한 설정 메소드
    public void authorizeUser() {
        this.role = Role.GUEST;
    }

    // 비밀번호 암호화 메소드
    public void passwordEncode(PasswordEncoder passwordEncoder) {
        this.password = passwordEncoder.encode(this.password);
    }

    public void updateRefreshToken(String updateRefreshToken) {
        this.refreshToken = updateRefreshToken;
    }
}