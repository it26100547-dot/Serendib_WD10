package com.serendib.controller;

import com.serendib.model.Booking;
import com.serendib.model.Hotel;
import com.serendib.model.TourPackage;
import com.serendib.model.Transport;
import com.serendib.model.User;
import com.serendib.model.dto.ApiResponse;
import com.serendib.security.SessionService;
import com.serendib.service.BookingService;
import com.serendib.service.FileStorageService;
import com.serendib.service.HotelService;
import com.serendib.service.TourPackageService;
import com.serendib.service.TransportService;
import jakarta.servlet.http.HttpSession;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

/**
 * Booking controller kept intentionally simple and explicit.
 * A logged-in user can create/view/pay/cancel their own bookings.
 */
@RestController
@RequestMapping("/api/bookings")
public class BookingController {
    private final BookingService bookingService;
    private final SessionService sessionService;
    private final TourPackageService packageService;
    private final HotelService hotelService;
    private final TransportService transportService;
    private final FileStorageService fileStorageService;

    public BookingController(BookingService bookingService,
                             SessionService sessionService,
                             TourPackageService packageService,
                             HotelService hotelService,
                             TransportService transportService,
                             FileStorageService fileStorageService) {
        this.bookingService = bookingService;
        this.sessionService = sessionService;
        this.packageService = packageService;
        this.hotelService = hotelService;
        this.transportService = transportService;
        this.fileStorageService = fileStorageService;
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse> myBookings(HttpSession session) {
        User user = sessionService.getCurrentUser(session);
        if (user == null)
            return unauthorizedList();
        return ResponseEntity.ok(ApiResponse.ok("Your bookings", bookingService.getForUser(user.getId())));
    }

    @PostMapping
    public ResponseEntity<ApiResponse> create(@RequestBody Booking booking, HttpSession session) {
        User user = sessionService.getCurrentUser(session);
        if (user == null)
            return unauthorizedBooking();

        try {
            booking.setUserId(user.getId());
            booking.setUserEmail(user.getEmail());
            booking.setCustomerName(user.getFullName());
            if (booking.getPhone() == null || booking.getPhone().isBlank())
                booking.setPhone(user.getPhone());

            calculatePrice(booking);
            booking.setStatus("PENDING_PAYMENT");
            booking.setPaymentStatus("PENDING");
            booking.setPaymentMethod("UNPAID");

            Booking saved = bookingService.create(booking);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.ok("Booking created. Please choose a payment method.", saved));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }



    @GetMapping("/{id}/receipt")
    public ResponseEntity<Resource> receipt(@PathVariable("id") Long id, HttpSession session) {
        User user = sessionService.getCurrentUser(session);
        if (user == null)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        try {
            Booking booking = getOwnedBooking(id, user);
            if (booking.getPaymentSlipFile() == null)
                return ResponseEntity.notFound().build();

            Path path = fileStorageService.getReceiptPath(booking.getPaymentSlipFile());
            if (!Files.exists(path))
                return ResponseEntity.notFound().build();

            Resource resource = new UrlResource(path.toUri());
            String contentType = Files.probeContentType(path);
            MediaType mediaType = contentType == null
                    ? MediaType.APPLICATION_OCTET_STREAM
                    : MediaType.parseMediaType(contentType);

            return ResponseEntity.ok()
                    .contentType(mediaType)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline")
                    .body(resource);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }





    private Booking getOwnedBooking(Long id, User user) {
        Booking booking = bookingService.getById(id);
        if (booking == null)
            throw new IllegalArgumentException("Booking not found.");

        if (!user.isAdmin() && !user.getId().equals(booking.getUserId())) {
            throw new IllegalArgumentException("You cannot access another user's booking.");
        }
        return booking;
    }

    /** Calculate the total using the actual selected package/hotel/vehicle. */
    private void calculatePrice(Booking booking) {
        String type = booking.getBookingType() == null ? "" : booking.getBookingType().toUpperCase();
        int guests = Math.max(1, booking.getGuests());

        if ("PACKAGE".equals(type)) {
            TourPackage p = packageService.getById(booking.getItemId());
            if (p == null)
                throw new IllegalArgumentException("Tour package not found.");
            checkStartDate(booking.getStartDate());
            booking.setItemName(p.getName());
            booking.setUnitPrice(p.getPrice());
            booking.setQuantity(guests);
            booking.setTotal(p.getPrice() * guests);
            return;
        }

        if ("HOTEL".equals(type)) {
            Hotel hotel = hotelService.getById(booking.getItemId());
            if (hotel == null)
                throw new IllegalArgumentException("Hotel not found.");

            Hotel.Room selectedRoom = null;
            for (Hotel.Room room : hotel.getRooms()) {
                if (booking.getOptionName() != null && booking.getOptionName().equalsIgnoreCase(room.getName())) {
                    selectedRoom = room;
                    break;
                }
            }
            if (selectedRoom == null && !hotel.getRooms().isEmpty())
                selectedRoom = hotel.getRooms().get(0);

            double pricePerNight = selectedRoom != null && selectedRoom.getPrice() != null
                    ? selectedRoom.getPrice() : hotel.getFromPrice();
            int nights = calculateDays(booking.getStartDate(), booking.getEndDate(), true);

            booking.setItemName(hotel.getName());
            if (selectedRoom != null)
                booking.setOptionName(selectedRoom.getName());
            booking.setUnitPrice(pricePerNight);
            booking.setQuantity(nights);
            booking.setTotal(pricePerNight * nights);
            return;
        }

        if ("TRANSPORT".equals(type)) {
            Transport transport = transportService.getById(booking.getItemId());
            if (transport == null)
                throw new IllegalArgumentException("Vehicle not found.");
            int days = calculateDays(booking.getStartDate(), booking.getEndDate(), false);
            booking.setItemName(transport.getName());
            booking.setUnitPrice(transport.getPrice());
            booking.setQuantity(days);
            booking.setTotal(transport.getPrice() * days);
            return;
        }

        throw new IllegalArgumentException("Booking type must be PACKAGE, HOTEL, or TRANSPORT.");
    }

    private void checkStartDate(String startDate) {
        try {
            LocalDate start = LocalDate.parse(startDate);
            if (start.isBefore(LocalDate.now()))
                throw new IllegalArgumentException("Booking date cannot be in the past.");
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            throw new IllegalArgumentException("Please select a valid booking date.");
        }
    }

    private int calculateDays(String startDate, String endDate, boolean endMustBeLater) {
        try {
            LocalDate start = LocalDate.parse(startDate);
            LocalDate end = LocalDate.parse(endDate);
            if (start.isBefore(LocalDate.now()))
                throw new IllegalArgumentException("Booking date cannot be in the past.");

            long difference = ChronoUnit.DAYS.between(start, end);
            if (difference < 0 || (endMustBeLater && difference == 0)) {
                throw new IllegalArgumentException("End date must be after the start date.");
            }
            return (int) Math.max(1, difference);
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            throw new IllegalArgumentException("Please select valid booking dates.");
        }
    }

    private ResponseEntity<ApiResponse> unauthorizedBooking() {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ApiResponse.error("Please sign in first."));
    }

    private ResponseEntity<ApiResponse> unauthorizedList() {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ApiResponse.error("Please sign in first."));
    }
}
