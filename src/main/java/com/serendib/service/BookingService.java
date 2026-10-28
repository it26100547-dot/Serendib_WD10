package com.serendib.service;

import com.serendib.model.Booking;
import com.serendib.repository.BookingRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class BookingService {
    private final BookingRepository repository;

    public BookingService(BookingRepository repository) {
        this.repository = repository;
    }

    public List<Booking> getAll() { return repository.findAll(); }
    public Booking getById(Long id) { return repository.findById(id); }
    public long count() { return repository.count(); }

    public List<Booking> getForUser(Long userId) {
        List<Booking> stored = repository.findByUserId(userId);
        List<Booking> newestFirst = new ArrayList<>();
        for (int i = stored.size() - 1; i >= 0; i--) {
            newestFirst.add(stored.get(i));
        }
        return newestFirst;
    }

    public Booking create(Booking booking) {
        booking.setId(null);
        validate(booking);
        return repository.save(booking);
    }





    private void validate(Booking booking) {
        if (booking.getBookingType() == null || booking.getBookingType().isBlank())
            throw new IllegalArgumentException("Booking type is required.");
        if (booking.getItemId() == null)
            throw new IllegalArgumentException("Booking item is required.");
        if (booking.getItemName() == null || booking.getItemName().isBlank())
            throw new IllegalArgumentException("Booking item name is required.");
    }
}
