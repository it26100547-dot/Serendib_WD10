package com.serendib.controller;

import com.serendib.model.dto.ApiResponse;
import com.serendib.security.SessionService;
import com.serendib.service.FileStorageService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/uploads")
public class UploadController {

    private final FileStorageService fileStorageService;
    private final SessionService sessionService;
    public UploadController(FileStorageService fileStorageService, SessionService sessionService) {
        this.fileStorageService = fileStorageService;
        this.sessionService = sessionService;
    }

    @PostMapping(consumes = "multipart/form-data")
    public ResponseEntity<ApiResponse> upload(@RequestPart("file") MultipartFile file, HttpSession session) {

        if (!sessionService.isAdmin(session)) {

            if (sessionService.getCurrentUser(session) == null)

                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ApiResponse.error("Please sign in first."));

            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ApiResponse.error("Administrator permission is required."));

        }
        try {

            return ResponseEntity.ok(ApiResponse.ok("Image uploaded.", Map.of("url", fileStorageService.saveImage(file))));

        } catch (IllegalArgumentException e) {

            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));

        }
    }
}
