package com.capstone.bowlingbling.domain.center.service;

import com.capstone.bowlingbling.domain.center.domain.Center;
import com.capstone.bowlingbling.domain.center.dto.request.CenterDetailRequestDto;
import com.capstone.bowlingbling.domain.center.dto.request.CenterSaveRequestDto;
import com.capstone.bowlingbling.domain.center.dto.response.CenterListResponseDto;
import com.capstone.bowlingbling.domain.center.repository.CenterRepository;
import com.capstone.bowlingbling.domain.image.service.S3ImageService;
import com.capstone.bowlingbling.domain.member.domain.Member;
import com.capstone.bowlingbling.domain.member.repository.MemberRepository;
import com.capstone.bowlingbling.global.enums.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
public class CenterService {

    private final CenterRepository centerRepository;
    private final MemberRepository memberRepository;
    private final S3ImageService s3ImageService;

    @Autowired
    public CenterService(CenterRepository centerRepository, MemberRepository memberRepository, S3ImageService s3ImageService) {
        this.centerRepository = centerRepository;
        this.memberRepository = memberRepository;
        this.s3ImageService = s3ImageService;
    }

    public void saveCenter(CenterSaveRequestDto centerDTO, String memberEmail, List<MultipartFile> files) throws IOException {
        Member owner = memberRepository.findByEmail(memberEmail)
                .orElseThrow(() -> new IllegalArgumentException("해당 이메일을 가진 사용자가 없습니다."));

        if (files != null && files.size() > 5) { // 최대 5개의 이미지만 허용
            throw new IllegalArgumentException("최대 5개의 이미지만 업로드할 수 있습니다.");
        }

        List<String> imageUrls = s3ImageService.uploadMultiple(files.toArray(new MultipartFile[0]));

        Center center = Center.builder()
                .owner(owner)
                .location(centerDTO.getLocation())
                .businessName(centerDTO.getBusinessName())
                .operatingHours(centerDTO.getOperatingHours())
                .announcements(centerDTO.getAnnouncements())
                .laneCount(centerDTO.getLaneCount())
                .images(imageUrls)
                .lat(centerDTO.getLat())
                .lng(centerDTO.getLng())
                .build();

        centerRepository.save(center);
    }

    public Page<CenterListResponseDto> getAllCenters(Pageable pageable) {
        Page<Center> centerPage = centerRepository.findAll(pageable);
        return centerPage.map(center -> CenterListResponseDto.builder()
                .id(center.getId())
                .businessName(center.getBusinessName())
                .location(center.getLocation())
                .operatingHours(center.getOperatingHours())
                .images(center.getImages())
                .laneCount(center.getLaneCount())
                .lat(center.getLat())
                .lng(center.getLng())
                .build());
    }

    public void updateCenter(Long centerId, CenterSaveRequestDto centerSaveRequestDto, List<MultipartFile> files, String memberEmail) throws IOException {
        Member member = memberRepository.findByEmail(memberEmail)
                .orElseThrow(() -> new IllegalArgumentException("해당 이메일을 가진 사용자가 없습니다."));

        Center center = centerRepository.findById(centerId)
                .orElseThrow(() -> new IllegalArgumentException("센터를 찾을 수 없습니다."));

        if (files != null && files.size() > 3) { // 최대 3개의 이미지만 허용
            throw new IllegalArgumentException("최대 3개의 이미지만 업로드할 수 있습니다.");
        }
        // 이미지 업로드 처리
        List<String> imageUrls = files != null ? s3ImageService.uploadMultiple(files.toArray(new MultipartFile[0])) : center.getImages();

        center = center.toBuilder()
                .businessName(centerSaveRequestDto.getBusinessName() != null ? centerSaveRequestDto.getBusinessName() : center.getBusinessName())
                .location(centerSaveRequestDto.getLocation() != null ? centerSaveRequestDto.getLocation() : center.getLocation())
                .operatingHours(centerSaveRequestDto.getOperatingHours() != null ? centerSaveRequestDto.getOperatingHours() : center.getOperatingHours())
                .announcements(centerSaveRequestDto.getAnnouncements() != null ? centerSaveRequestDto.getAnnouncements() : center.getAnnouncements())
                .laneCount(centerSaveRequestDto.getLaneCount() != null ? centerSaveRequestDto.getLaneCount() : center.getLaneCount())
                .images(imageUrls)
                .build();

        centerRepository.save(center);
    }

    public CenterDetailRequestDto getCenterDetails(Long id) {
        Center center = centerRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 센터를 찾을 수 없습니다."));

        return CenterDetailRequestDto.builder()
                .ownerName(center.getOwner().getName())
                .businessName(center.getBusinessName())
                .location(center.getLocation())
                .announcements(center.getAnnouncements())
                .laneCount(center.getLaneCount())
                .images(center.getImages())
                .operatingHours(center.getOperatingHours())
                .lat(center.getLat())
                .lng(center.getLng())
                .build();
    }

    public void deleteCenter(Long centerId, String memberEmail) {
        // Center 정보 가져오기
        Center center = centerRepository.findById(centerId)
                .orElseThrow(() -> new IllegalArgumentException("해당 센터가 존재하지 않습니다."));

        Member member = memberRepository.findByEmail(memberEmail)
                .orElseThrow(() -> new IllegalArgumentException("해당 이메일을 가진 사용자가 없습니다."));

        // 소유자 또는 ADMIN이 아니면 예외 발생
        if (!center.getOwner().getEmail().equals(memberEmail) && !member.getRole().equals(Role.ADMIN)) {
            throw new SecurityException("삭제 권한이 없습니다.");
        }

        // 권한이 있으면 센터 삭제
        centerRepository.delete(center);
    }
}
