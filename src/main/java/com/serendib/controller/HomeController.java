package com.serendib.controller;

import com.serendib.model.dto.ApiResponse;
import com.serendib.service.HomeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/home")
public class HomeController {
    private final HomeService homeService;
    public HomeController(HomeService homeService) {
        this.homeService = homeService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse> getHomePageData() {
        return ResponseEntity.ok(ApiResponse.ok("Home page data loaded.", homeService.getHomePageData()));
    }
}
