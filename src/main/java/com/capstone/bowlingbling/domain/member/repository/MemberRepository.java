package com.capstone.bowlingbling.domain.member.repository;

import com.capstone.bowlingbling.domain.club.domain.Club;
import com.capstone.bowlingbling.domain.member.domain.Member;
import com.capstone.bowlingbling.global.enums.ClubRole;
import com.capstone.bowlingbling.global.enums.SocialType;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findByEmail(String email);

    Optional<Member> findByNickname(String nickname);

    Optional<Member> findByRefreshToken(String refreshToken);

    boolean existsByEmail(String email);

    @Modifying
    @Query("UPDATE Member m SET m.refreshToken = null WHERE m.email = :email")
    void deleteRefreshTokenByEmail(@Param("email") String email);

    @Modifying
    @Transactional
    @Query("UPDATE Member m SET m.name = :name, m.nickname = :nickname, m.email = :email, " +
            "m.image = :image, m.phonenum = :phonenum, m.city = :city, m.sex = :sex, " +
            "m.age = :age, m.introduction = :introduction, m.myaver = :myaver WHERE m.email = :currentEmail")
    void updateProfile(@Param("name") String name, @Param("nickname") String nickname,
                       @Param("email") String email, @Param("image") String image,
                       @Param("phonenum") String phonenum, @Param("city") String city,
                       @Param("sex") String sex, @Param("age") Integer age,
                       @Param("introduction") String introduction, @Param("myaver") Integer myaver,
                       @Param("currentEmail") String currentEmail);

    @Modifying
    @Query("UPDATE Member m SET m.club = :club, m.clubRole = :clubRole, m.clubJoinedAt = :clubJoinedAt WHERE m.email = :email")
    void updateMemberClubInfo(@Param("email") String email,
                              @Param("club") Club club,
                              @Param("clubRole") ClubRole clubRole,
                              @Param("clubJoinedAt") String clubJoinedAt);

    @Modifying
    @Query("UPDATE Member m SET m.clubRole = :clubRole WHERE m.id = :userId AND m.club.id = :clubId")
    void updateMemberRole(@Param("userId") Long userId,
                          @Param("clubId") Long clubId,
                          @Param("clubRole") ClubRole clubRole);

    /**
     * 소셜 타입과 소셜의 식별값으로 회원 찾는 메소드
     * 정보 제공을 동의한 순간 DB에 저장해야하지만, 아직 추가 정보(사는 도시, 나이 등)를 입력받지 않았으므로
     * 유저 객체는 DB에 있지만, 추가 정보가 빠진 상태이다.
     * 따라서 추가 정보를 입력받아 회원 가입을 진행할 때 소셜 타입, 식별자로 해당 회원을 찾기 위한 메소드
     */
    Optional<Member> findBySocialTypeAndSocialId(SocialType socialType, String socialId);
}
