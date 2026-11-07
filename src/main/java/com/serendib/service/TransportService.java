package com.serendib.service;

import com.serendib.model.Transport;
import com.serendib.repository.TransportRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class TransportService {
    private final TransportRepository repository;
    public TransportService(TransportRepository repository) { this.repository = repository; }

    public List<Transport> getAll() { return repository.findAll(); }
    public Transport getById(Long id) { return repository.findById(id); }
    public List<Transport> getByType(String type) { return repository.findByType(type); }
    public List<Transport> getAvailable() { return repository.findAvailable(); }
    public long count() { return repository.count(); }

    public Transport create(Transport transport) { transport.setId(null); validate(transport); return repository.save(transport); }
    //Adding update operation
    public Transport update(Long id, Transport transport) {
        Transport existing = repository.findById(id);
        if (existing == null) {
            throw new IllegalArgumentException("Transport with ID " + id + " does not exist.");
        }


        transport.setId(id);
        validate(transport);

        return repository.save(transport);
    }


    private void validate(Transport transport) {
        if (transport.getName() == null || transport.getName().isBlank()) throw new IllegalArgumentException("Vehicle name is required.");
        if (transport.getPrice() < 0) throw new IllegalArgumentException("Price cannot be negative.");
        if (transport.getCapacity() < 1) transport.setCapacity(4);
    }
}
