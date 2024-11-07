package com.capstone.bowlingbling.domain.lessoninfo.repository;

import com.capstone.bowlingbling.domain.lessoninfo.domain.LessonInfo;
import com.capstone.bowlingbling.domain.member.domain.Member;
import com.capstone.bowlingbling.global.enums.BowlingStyle;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LessonInfoRepository extends JpaRepository<LessonInfo, Long> {
    boolean existsByMemberAndDeletedAtIsNull(Member member);

    @Modifying
    @Transactional
    @Query("UPDATE LessonInfo l SET " +
            "l.title = COALESCE(:title, l.title), " +
            "l.intro = COALESCE(:introduction, l.intro), " +
            "l.contents = COALESCE(:contents, l.contents), " +
            "l.qualifications = COALESCE(:qualifications, l.qualifications), " +
            "l.careerHistory = COALESCE(:careerHistory, l.careerHistory), " +
            "l.program = COALESCE(:program, l.program), " +
            "l.address = COALESCE(:location, l.address), " +
            "l.operatingHours = COALESCE(:operatingHours, l.operatingHours), " +
            "l.lat = COALESCE(:lat, l.lat), " +
            "l.lng = COALESCE(:lng, l.lng), " +
            "l.place = COALESCE(:place, l.place), " +
            "l.category = COALESCE(:category, l.category), " +
            "l.price = COALESCE(:price, l.price), " +
            "l.hasFreeParking = COALESCE(:hasFreeParking, l.hasFreeParking) " +
            "WHERE l.id = :id")
    void updateLessonInfo(Long id, String title, String introduction, String contents, String qualifications,
                          String careerHistory, BowlingStyle program, String location, String operatingHours,
                          String lat, String lng, String place, String category, Integer price, Boolean hasFreeParking);

    Page<LessonInfo> findAllByDeletedAtIsNull(Pageable pageable);
}
