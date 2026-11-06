package com.serendib.service;

import com.serendib.model.Destination;
import com.serendib.repository.DestinationRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class DestinationService {
    private final DestinationRepository repository;
    public DestinationService(DestinationRepository repository) { this.repository = repository; }

    public List<Destination> getAll() { return repository.findAll(); }
    public Destination getById(Long id) { return repository.findById(id); }
    public List<Destination> getByRegion(String region) { return repository.findByRegion(region); }
    public List<Destination> getFeatured() { return repository.findFeatured(); }
    public long count() { return repository.count(); }

    public Destination create(Destination destination) {
        destination.setId(null);
        validate(destination);
        return repository.save(destination);
    }

    // adding upadate
    public Destination update(Long id, Destination destination) {

        Destination existing = repository.findById(id);
        if (existing == null) {
            throw new IllegalArgumentException("Destination with ID " + id + " not found.");
        }

        destination.setId(id);

        validate(destination);

        return repository.save(destination);
    }

    // adding delete
    public boolean delete(Long id) {

        if (!repository.existsById(id)) {
            return false;
        }
        return repository.deleteById(id);
    }


    private void validate(Destination destination) {
        if (destination.getName() == null || destination.getName().isBlank())
            throw new IllegalArgumentException("Destination name is required.");
        if (destination.getRegion() == null || destination.getRegion().isBlank()) destination.setRegion("Central");
    }
}
