package com.serendib.service;

import com.serendib.model.Hotel;
import com.serendib.model.TourPackage;
import com.serendib.model.Transport;
import com.serendib.repository.HotelRepository;
import com.serendib.repository.TourPackageRepository;
import com.serendib.repository.TransportRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class TourPackageService {
    private final TourPackageRepository repository;
    private final TransportRepository transportRepository;
    private final HotelRepository hotelRepository;

    public TourPackageService(TourPackageRepository repository, TransportRepository transportRepository,
                              HotelRepository hotelRepository) {
        this.repository = repository;
        this.transportRepository = transportRepository;
        this.hotelRepository = hotelRepository;
    }

    public List<TourPackage> getAll() {
        return repository.findAll();
    }
    public TourPackage getById(Long id) {
        return repository.findById(id);
    }
    public List<TourPackage> getFeatured() {
        return repository.findFeatured();
    }
    public List<TourPackage> getByCategory(String category) {
        return repository.findByCategory(category);
    }
    public List<TourPackage> filter(String search, String category, Double maxPrice, Integer maxDays) {
        return repository.filter(search, category, maxPrice, maxDays);
    }
    public long count() {
        return repository.count();
    }

    public TourPackage create(TourPackage p) {
        p.setId(null); validateAndFillNames(p); return repository.save(p);
    }



    private void validateAndFillNames(TourPackage p) {
        if (p.getName() == null || p.getName().isBlank())
            throw new IllegalArgumentException("Package name is required.");
        if (p.getPrice() < 0)
            throw new IllegalArgumentException("Package price cannot be negative.");
        if (p.getNormalPrice() < p.getPrice())
            p.setNormalPrice(p.getPrice());

        if (p.getTransportId() != null) {
            Transport t = transportRepository.findById(p.getTransportId());
            if (t != null) p.setTransportName(t.getName());
        }
        if (p.getHotelId() != null) {
            Hotel h = hotelRepository.findById(p.getHotelId());
            if (h != null) p.setHotelName(h.getName());
        }
    }
}
