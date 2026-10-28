package com.serendib.controller;

import com.serendib.model.Destination;
import com.serendib.model.dto.ApiResponse;
import com.serendib.security.SessionService;
import com.serendib.service.DestinationService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/destinations")
public class DestinationController {
    private final DestinationService service;
    private final SessionService sessionService;

    public DestinationController(DestinationService service, SessionService sessionService) {
        this.service = service;
        this.sessionService = sessionService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse> getAll(@RequestParam(name = "region", required = false) String region) {
        List<Destination> list = (region == null || region.isBlank()) ? service.getAll() : service.getByRegion(region);
        return ResponseEntity.ok(ApiResponse.ok(list));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> getById(@PathVariable("id") Long id) {
        Destination item = service.getById(id);
        if (item == null)
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.error("Destination not found."));
        return ResponseEntity.ok(ApiResponse.ok(item));
    }

    @PostMapping
    public ResponseEntity<ApiResponse> create(@RequestBody Destination destination, HttpSession session) {
        if (!sessionService.isAdmin(session))
            return adminOnly(session);
        try {
            Destination saved = service.create(destination);
            return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok("Destination created successfully.", saved));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }



    private ResponseEntity<ApiResponse> adminOnly(HttpSession session) {
        if (sessionService.getCurrentUser(session) == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ApiResponse.error("Please sign in first."));
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ApiResponse.error("Administrator permission is required."));
    }
}
