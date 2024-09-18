package com.capstone.bowlingbling.domain.image.controller;

import com.capstone.bowlingbling.domain.image.service.S3ImageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/filetest")
public class S3TestController {

    @Autowired
    S3ImageService s3ImageService;

    @PostMapping("/upload")
    public String upload(@RequestParam("file") MultipartFile file) throws Exception {
        s3ImageService.upload(file);
        return "File uploaded Successfully";
    }

    @Operation(summary = "Upload multiple files to S3")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Files uploaded successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    @PostMapping("/uploadMultiple")
    public ResponseEntity<List<String>> uploadMultiple(@RequestParam("files") MultipartFile[] files) throws Exception {
        List<String> imageUrls = s3ImageService.uploadMultiple(files);
        return ResponseEntity.ok(imageUrls);
    }

}
