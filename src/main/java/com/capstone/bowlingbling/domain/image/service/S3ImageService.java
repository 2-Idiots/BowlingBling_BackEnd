package com.capstone.bowlingbling.domain.image.service;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.util.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
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

    public ResponseEntity<byte[]> downloadFileFromS3(String fileUrl, String fileName) throws IOException {
        // S3에서 파일 다운로드
        S3Object s3Object = amazonS3.getObject(new GetObjectRequest(bucketName, fileUrl));

        // S3Object에서 파일 데이터를 가져오기
        InputStream inputStream = s3Object.getObjectContent();

        try {
            // InputStream을 byte[]로 변환
            byte[] fileBytes = IOUtils.toByteArray(inputStream);

            // 파일명이 한글일 경우, UTF-8로 인코딩하여 처리
            String encodedFileName = URLEncoder.encode(fileName, "UTF-8")
                    .replaceAll("\\+", " "); // 공백은 +로 변환되지 않도록 처리

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDisposition(ContentDisposition.attachment()
                    .filename(encodedFileName)
                    .build());

            // 파일 바이트를 반환하는 ResponseEntity 생성
            return new ResponseEntity<>(fileBytes, headers, HttpStatus.OK);
        } catch (IOException e) {
            throw new RuntimeException("파일 다운로드 중 오류가 발생했습니다.", e);
        }
    }

    public List<String> uploadClubMultiple(MultipartFile[] files) throws AmazonServiceException, IOException, SdkClientException {
        List<String> imageUrls = new ArrayList<>();

        for (MultipartFile file : files) {
            String fileName = file.getOriginalFilename();
            if (fileName != null) {
                validateClubBoardFile(fileName); // 유효성 검사 함수 호출
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

    private void validateClubBoardFile(String fileName) {
        if (fileName == null || fileName.trim().isEmpty()) {
            throw new IllegalArgumentException("파일 이름이 비어 있습니다.");
        }

        // 마지막 점(.)이 존재하지 않는 파일명 처리
        int extensionIndex = fileName.lastIndexOf(".");
        if (extensionIndex == -1) {
            throw new IllegalArgumentException("파일 확장자를 찾을 수 없습니다.");
        }

        String fileExtension = fileName.substring(extensionIndex).toLowerCase(); // 소문자로 변환하여 검사
        List<String> allowedExtensions = Arrays.asList(".jpg", ".jpeg", ".png", ".gif", ".pdf", ".dox", ".docx", ".xls", ".xlsx", ".txt");

        if (!allowedExtensions.contains(fileExtension)) {
            throw new IllegalArgumentException("지원되지 않는 파일 형식입니다: " + fileExtension);
        }
    }
}
