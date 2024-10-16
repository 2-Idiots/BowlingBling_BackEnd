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
    @Query("UPDATE LessonInfo l SET l.title = :title, l.intro = :intro, l.contents = :contents, " +
            "l.qualifications = :qualifications, l.careerHistory = :careerHistory, " +
            "l.program = :program, l.address = :location, l.operatingHours = :operatingHours, " +
            "l.images = :images, l.lat = :lat, l.lng = :lng, l.place = :place, " +
            "l.category = :category, l.price = :price, l.hasFreeParking = :hasFreeParking WHERE l.id = :id")
    void updateLessonInfo(@Param("id") Long id,
                          @Param("title") String title,
                          @Param("intro") String intro,
                          @Param("contents") String contents,
                          @Param("qualifications") String qualifications,
                          @Param("careerHistory") String careerHistory,
                          @Param("program") BowlingStyle program,
                          @Param("location") String location,
                          @Param("operatingHours") String operatingHours,
                          @Param("images") List<String> images,
                          @Param("lat") String lat,
                          @Param("lng") String lng,
                          @Param("place") String place,
                          @Param("category") String category,
                          @Param("price") Integer price,
                          @Param("hasFreeParking") Boolean hasFreeParking);

    Page<LessonInfo> findAllByDeletedAtIsNull(Pageable pageable);

    @Query("SELECT l FROM Member m JOIN m.likedLessons l WHERE m = :member AND l.deletedAt IS NULL")
    List<LessonInfo> findLikedLessonsByMember(@Param("member") Member member);
}
