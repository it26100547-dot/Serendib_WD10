package com.serendib.controller;

import com.serendib.model.Hotel;
import com.serendib.model.dto.ApiResponse;
import com.serendib.security.SessionService;
import com.serendib.service.HotelService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/hotels")
public class HotelController {
    private final HotelService service;
    private final SessionService sessionService;

    public HotelController(HotelService service, SessionService sessionService) {
        this.service = service;
        this.sessionService = sessionService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse> getAll(@RequestParam(name = "destination", required = false) String destination) {
        List<Hotel> list = (destination == null || destination.isBlank()) ? service.getAll() : service.getByDestination(destination);
        return ResponseEntity.ok(ApiResponse.ok(list));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> getById(@PathVariable("id") Long id) {
        Hotel item = service.getById(id);
        if (item == null)
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.error("Hotel not found."));
        return ResponseEntity.ok(ApiResponse.ok(item));
    }

    @PostMapping
    public ResponseEntity<ApiResponse> create(@RequestBody Hotel hotel, HttpSession session) {
        if (!sessionService.isAdmin(session))
            return adminOnly(session);
        try {
            Hotel saved = service.create(hotel);
            return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok("Hotel created successfully.", saved));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
    // PUT to update a hotel
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse> update(@PathVariable("id") Long id, @RequestBody Hotel hotel, HttpSession session) {

        if (!sessionService.isAdmin(session)) {
            return adminOnly(session);
        }
        try {
            Hotel updatedHotel = service.update(id, hotel);
            return ResponseEntity.ok(ApiResponse.ok("Hotel updated successfully.", updatedHotel));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
    // DELETE to delete a hotel
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> delete(@PathVariable("id") Long id, HttpSession session) {

        if (!sessionService.isAdmin(session)) {
            return adminOnly(session);
        }
        try {
            service.delete(id);
            return ResponseEntity.ok(ApiResponse.ok("Hotel deleted successfully.", null));
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
