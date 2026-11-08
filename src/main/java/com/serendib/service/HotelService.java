package com.serendib.service;

import com.serendib.model.Hotel;
import com.serendib.repository.HotelRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class HotelService {

    private final HotelRepository repository;
    public HotelService(HotelRepository repository) {
        this.repository = repository;
    }

    public List<Hotel> getAll() {
        return repository.findAll();
    }
    public Hotel getById(Long id) {
        return repository.findById(id);
    }
    public List<Hotel> getByDestination(String destination) {
        return repository.findByDestination(destination);
    }
    public List<Hotel> getByMaxPrice(double maxPrice) {
        return repository.findByMaxPrice(maxPrice);
    }
    public long count() {
        return repository.count();
    }

    public Hotel create(Hotel hotel) {
        hotel.setId(null); validate(hotel); return repository.save(hotel);
    }

    // Method to update hotel

    public Hotel update(Long id, Hotel hotelDetails) {

        Hotel existingHotel = repository.findById(id);

        if (existingHotel == null) {
            throw new IllegalArgumentException("Hotel not found with ID: " + id);
        }

        validate(hotelDetails);

        hotelDetails.setId(id);
        return repository.save(hotelDetails);
    }

    // Method to delete a hotel
    public boolean delete(Long id) {

        if (!repository.existsById(id)) {

            throw new IllegalArgumentException("Hotel not found with ID: " + id);
        }
        return repository.deleteById(id);
    }



    private void validate(Hotel hotel) {

        if (hotel.getName() == null || hotel.getName().isBlank())
            throw new IllegalArgumentException("Hotel name is required.");

        if (hotel.getFromPrice() < 0)
            throw new IllegalArgumentException("Hotel price cannot be negative.");
    }
}
