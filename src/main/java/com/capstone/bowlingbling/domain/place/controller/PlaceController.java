package com.capstone.bowlingbling.domain.place.controller;

import com.capstone.bowlingbling.domain.place.service.PlaceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "Place", description = "Place 검색 API")
public class PlaceController {

    private final PlaceService placeService;

    public PlaceController(PlaceService placeService) {
        this.placeService = placeService;
    }

    @GetMapping("/search")
    @Operation(summary = "키워드로 장소 검색", description = "키워드를 사용하여 장소를 검색합니다.")
    public String searchKeyword(@RequestParam String query) {
        return placeService.searchKeyword(query);
    }
}
