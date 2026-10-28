package com.serendib.controller;

import com.serendib.model.Booking;
import com.serendib.model.dto.ApiResponse;
import com.serendib.security.SessionService;
import com.serendib.service.BookingService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/bookings")
public class AdminBookingController {
    private final BookingService bookingService; private final SessionService sessionService;
    public AdminBookingController(BookingService bookingService, SessionService sessionService) { this.bookingService = bookingService; this.sessionService = sessionService; }

    @GetMapping
    public ResponseEntity<ApiResponse> getAll(HttpSession session) {
        if (!sessionService.isAdmin(session)) return adminOnly(session);
        List<Booking> list = bookingService.getAll();
        list.sort((a, b) -> Long.compare(b.getId() == null ? 0 : b.getId(), a.getId() == null ? 0 : a.getId()));
        return ResponseEntity.ok(ApiResponse.ok("All bookings", list));
    }



    private ResponseEntity<ApiResponse> adminOnly(HttpSession session) {
        if (sessionService.getCurrentUser(session) == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ApiResponse.error("Please sign in first."));
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ApiResponse.error("Administrator permission is required."));
    }
}
