package com.capstone.bowlingbling.domain.member.repository;

import com.capstone.bowlingbling.domain.member.domain.TeacherRequest;
import com.capstone.bowlingbling.global.enums.TeacherStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TeacherRequestRepository extends JpaRepository<TeacherRequest, Long> {
    Page<TeacherRequest> findAllByStatus(TeacherStatus status, Pageable pageable);
}
