package com.serendib.controller;

import com.serendib.model.TourPackage;
import com.serendib.model.dto.ApiResponse;
import com.serendib.security.SessionService;
import com.serendib.service.TourPackageService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/** Complete Tour Package CRUD API. */
@RestController
@RequestMapping("/api/packages")
public class TourPackageController {
    private final TourPackageService service;
    private final SessionService sessionService;

    public TourPackageController(TourPackageService service, SessionService sessionService) {
        this.service = service;
        this.sessionService = sessionService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse> getAll(
            @RequestParam(name = "search", required = false) String search,
            @RequestParam(name = "category", required = false) String category,
            @RequestParam(name = "maxPrice", required = false) Double maxPrice,
            @RequestParam(name = "maxDays", required = false) Integer maxDays) {
        List<TourPackage> list = service.filter(search, category, maxPrice, maxDays);
        return ResponseEntity.ok(ApiResponse.ok(list));
    }

    @GetMapping("/featured")
    public ResponseEntity<ApiResponse> featured() {
        return ResponseEntity.ok(ApiResponse.ok(service.getFeatured()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> getById(@PathVariable("id") Long id) {
        TourPackage item = service.getById(id);
        if (item == null)
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.error("Tour package not found."));

        return ResponseEntity.ok(ApiResponse.ok(item));
    }

    @PostMapping
    public ResponseEntity<ApiResponse> create(@RequestBody TourPackage tourPackage, HttpSession session) {
        if (!sessionService.isAdmin(session))
            return adminOnly(session);
        try {
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.ok("Tour package created successfully.", service.create(tourPackage)));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse> update(@PathVariable("id") Long id, @RequestBody TourPackage tourPackage, HttpSession session) {
        // Check if user is admin
        if (!sessionService.isAdmin(session)) {
            return adminOnly(session);
        }

        try {
            TourPackage updatedItem = service.update(id, tourPackage);
            return ResponseEntity.ok(ApiResponse.ok("Tour package updated successfully.", updatedItem));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> delete(@PathVariable("id") Long id, HttpSession session) {
        // Check if user is admin
        if (!sessionService.isAdmin(session)) {
            return adminOnly(session);
        }

        try {
            service.delete(id);
            return ResponseEntity.ok(ApiResponse.ok("Tour package deleted successfully.", null));
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
