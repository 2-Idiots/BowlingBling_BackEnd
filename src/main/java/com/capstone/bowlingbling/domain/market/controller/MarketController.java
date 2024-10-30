package com.capstone.bowlingbling.domain.market.controller;

import com.capstone.bowlingbling.domain.market.dto.request.RequestMarketSaveDTO;
import com.capstone.bowlingbling.domain.market.dto.response.ResponseMarketDetailDTO;
import com.capstone.bowlingbling.domain.market.dto.response.ResponseMarketListDTO;
import com.capstone.bowlingbling.domain.market.service.MarketService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@Tag(name = "markets", description = "중고장터 API")
@RequestMapping("/market")
public class MarketController {

    @Autowired
    private MarketService marketService;

    @Operation(summary = "중고마켓 신규저장", description = "중고마켓 신규저장")
    @PostMapping(value = "/save", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> createMarket(@RequestPart(value = "request") RequestMarketSaveDTO requestMarketSaveDTO,
                                               @AuthenticationPrincipal User sessionMember,
                                               @Parameter(description = "업로드할 파일 목록", content = @Content(mediaType = MediaType.MULTIPART_FORM_DATA_VALUE))
                                               @RequestPart(value = "files", required = false) List<MultipartFile> files) throws IOException {
        try {
            String memberEmail = sessionMember.getUsername();
            marketService.saveMarket(requestMarketSaveDTO, memberEmail, files);
            return new ResponseEntity<>("물품이 성공적으로 저장되었습니다", HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>("사용자가 인증되지 않았습니다", HttpStatus.UNAUTHORIZED);
        }
    }

    @Operation(summary = "중고장터 전체 목록 조회")
    @GetMapping
    public Page<ResponseMarketListDTO> getMarketList(@RequestParam(defaultValue = "0") int page) {
        return marketService.getAllMarkets(PageRequest.of(page, 10));
    }

    @Operation(summary = "중고장터 상세 품목 조회")
    @GetMapping("/{id}")
    public ResponseEntity<ResponseMarketDetailDTO> getMarketDetails(@PathVariable Long id) {
        ResponseMarketDetailDTO marketDTO = marketService.getMarketDetail(id);
        if (marketDTO != null) {
            return ResponseEntity.ok(marketDTO);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "중고장터 상품 정보 수정")
    @PatchMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> updateMarket(@PathVariable Long id,
                                               @RequestBody RequestMarketSaveDTO requestMarketSaveDTO,
                                               @AuthenticationPrincipal User sessionMember,
                                               @Parameter(description = "업로드할 파일 목록", content = @Content(mediaType = MediaType.MULTIPART_FORM_DATA_VALUE))
                                                   @RequestPart(value = "files", required = false) List<MultipartFile> files) throws IOException {
        try{
            String memberEmail = sessionMember.getUsername();
            marketService.updateMarket(id, requestMarketSaveDTO, memberEmail, files);
            return ResponseEntity.ok("상품이 성공적으로 수정되었습니다");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "상품 삭제")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteMarket(@PathVariable Long id, @AuthenticationPrincipal User sessionMember) {
        try{
            String memberEmail = sessionMember.getUsername();
            marketService.deleteMarket(id, memberEmail);
            return ResponseEntity.ok("상품이 성공적으로 삭제되었습니다.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
