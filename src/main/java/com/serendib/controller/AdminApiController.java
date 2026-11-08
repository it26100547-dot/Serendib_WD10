package com.serendib.controller;

import com.serendib.model.dto.AdminStatsDto;
import com.serendib.model.dto.ApiResponse;
import com.serendib.security.SessionService;
import com.serendib.service.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
public class AdminApiController {
    private final TourPackageService packageService;
    private final DestinationService destinationService;
    private final HotelService hotelService;
    private final TransportService transportService;
    private final UserService userService;
    private final BookingService bookingService;
    private final SessionService sessionService;

    public AdminApiController(TourPackageService packageService, DestinationService destinationService,HotelService hotelService, TransportService transportService, UserService userService, BookingService bookingService, SessionService sessionService) {
        this.packageService = packageService;
        this.destinationService = destinationService;
        this.hotelService = hotelService;
        this.transportService = transportService;
        this.userService = userService;
        this.bookingService = bookingService;
        this.sessionService = sessionService;
    }

    @GetMapping("/stats")
    public ResponseEntity<ApiResponse> stats(HttpSession session) {
        if (!sessionService.isAdmin(session)) {
            if (sessionService.getCurrentUser(session) == null)
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ApiResponse.error("Please sign in first."));
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ApiResponse.error("Administrator permission is required."));
        }
        AdminStatsDto stats = new AdminStatsDto(packageService.count(), destinationService.count(), hotelService.count(), transportService.count(), userService.count());
        stats.setTotalBookings(bookingService.count());
        return ResponseEntity.ok(ApiResponse.ok(stats));
    }
}
