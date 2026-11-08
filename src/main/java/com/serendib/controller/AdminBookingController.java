package com.serendib.controller;

import com.serendib.model.Booking;
import com.serendib.model.dto.ApiResponse;
import com.serendib.security.SessionService;
import com.serendib.service.BookingService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/bookings")
public class AdminBookingController {
    private final BookingService bookingService;
    private final SessionService sessionService;

    public AdminBookingController(BookingService bookingService, SessionService sessionService) {
        this.bookingService = bookingService;
        this.sessionService = sessionService;
    }

    // GET /api/admin/bookings
    @GetMapping
    public ResponseEntity<ApiResponse> getAll(HttpSession session) {
        if (!sessionService.isAdmin(session)) {
            return adminOnly(session);
        }
        List<Booking> list = bookingService.getAll();
        list.sort((a, b) -> Long.compare(b.getId() == null ? 0 : b.getId(), a.getId() == null ? 0 : a.getId()));
        return ResponseEntity.ok(ApiResponse.ok("All bookings", list));
    }

    // PUT /api/admin/bookings/{id}/status
    @PutMapping("/{id}/status")
    public ResponseEntity<ApiResponse> updateStatus(@PathVariable("id") Long id, @RequestBody Map<String, Object> body,HttpSession session) {
        if (!sessionService.isAdmin(session)) {
            return adminOnly(session);
        }

        try {
            Booking booking = bookingService.getById(id);
            if (booking == null) {
                return ResponseEntity.badRequest().body(ApiResponse.error("Booking not found."));
            }

            if (body.containsKey("status") && body.get("status") != null) {
                booking.setStatus(body.get("status").toString());
            }
            if (body.containsKey("paymentStatus") && body.get("paymentStatus") != null) {
                booking.setPaymentStatus(body.get("paymentStatus").toString());
            }
            if (body.containsKey("adminNote")) {
                booking.setAdminNote(body.get("adminNote") != null ? body.get("adminNote").toString() : "");
            }

            Booking saved = bookingService.update(id, booking);
            return ResponseEntity.ok(ApiResponse.ok("Booking status updated.", saved));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    // PUT /api/admin/bookings/{id} admin update operation
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse> updateBooking(@PathVariable("id") Long id,@RequestBody Booking updatedBooking,HttpSession session) {

        if (!sessionService.isAdmin(session)) {
            return adminOnly(session);
        }

        try {
            Booking saved = bookingService.update(id, updatedBooking);
            if (saved == null) {
                return ResponseEntity.badRequest().body(ApiResponse.error("Booking not found with id: " + id));
            }
            return ResponseEntity.ok(ApiResponse.ok("Booking updated successfully.", saved));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    // DELETE /api/admin/bookings/{id} Admin delete operation
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> adminDeleteBooking(@PathVariable("id") Long id, HttpSession session) {
        if (!sessionService.isAdmin(session)) {
            return adminOnly(session);
        }

        boolean deleted = bookingService.delete(id);
        if (deleted) {
            return ResponseEntity.ok(ApiResponse.ok("Booking deleted by Admin.", null));
        }
        return ResponseEntity.badRequest().body(ApiResponse.error("Booking not found or failed to delete."));
    }

    private ResponseEntity<ApiResponse> adminOnly(HttpSession session) {
        if (sessionService.getCurrentUser(session) == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ApiResponse.error("Please sign in first."));
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ApiResponse.error("Administrator permission is required."));
    }
}