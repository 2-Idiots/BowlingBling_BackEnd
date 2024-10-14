package com.capstone.bowlingbling.domain.image.service;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
@Service
public class S3ImageService {

    @Autowired
    private AmazonS3 amazonS3;

    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;

    public String upload(MultipartFile file) throws AmazonServiceException, IOException, SdkClientException {
        ObjectMetadata metadata = new ObjectMetadata();
        amazonS3.putObject(bucketName, file.getOriginalFilename(), file.getInputStream(), metadata);

        String imageUrl = amazonS3.getUrl(bucketName, file.getOriginalFilename()).toString();

        return imageUrl;
    }

    public List<String> uploadMultiple(MultipartFile[] files) throws AmazonServiceException, IOException, SdkClientException {
        List<String> imageUrls = new ArrayList<>();

        for (MultipartFile file : files) {
            String fileName = file.getOriginalFilename();
            if (fileName != null) {
                validateFile(fileName); // 유효성 검사 함수 호출
            } else {
                throw new IllegalArgumentException("파일명이 유효하지 않습니다.");
            }

            ObjectMetadata metadata = new ObjectMetadata();
            amazonS3.putObject(bucketName, file.getOriginalFilename(), file.getInputStream(), metadata);

            String imageUrl = amazonS3.getUrl(bucketName, file.getOriginalFilename()).toString();
            imageUrls.add(imageUrl);
        }
        return imageUrls;
    }

    public String extractFileName(String imageUrl) {
        // URL에서 마지막 슬래시 이후의 문자열을 추출하여 파일명으로 반환
        if (imageUrl != null && !imageUrl.isEmpty()) {
            return imageUrl.substring(imageUrl.lastIndexOf("/") + 1);
        } else {
            throw new IllegalArgumentException("유효하지 않은 이미지 URL입니다.");
        }
    }



    public void deleteFile(String fileName) {
        amazonS3.deleteObject(new DeleteObjectRequest(bucketName, fileName));
    }

    // 파일 유효성 검사
    private void validateFile(String fileName) {
        if (fileName == null || fileName.trim().isEmpty()) {
            throw new IllegalArgumentException("파일 이름이 비어 있습니다.");
        }

        // 마지막 점(.)이 존재하지 않는 파일명 처리
        int extensionIndex = fileName.lastIndexOf(".");
        if (extensionIndex == -1) {
            throw new IllegalArgumentException("파일 확장자를 찾을 수 없습니다.");
        }

        String fileExtension = fileName.substring(extensionIndex).toLowerCase(); // 소문자로 변환하여 검사
        List<String> allowedExtensions = Arrays.asList(".jpg", ".jpeg", ".png");

        if (!allowedExtensions.contains(fileExtension)) {
            throw new IllegalArgumentException("지원되지 않는 파일 형식입니다: " + fileExtension);
        }
    }
}
