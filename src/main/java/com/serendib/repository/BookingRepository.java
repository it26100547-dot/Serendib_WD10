package com.serendib.repository;

import com.serendib.model.Booking;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;


@Repository
public class BookingRepository {
    private final Path filePath;
    private final ObjectMapper mapper = new ObjectMapper();

    public BookingRepository(@Value("${serendib.database.dir:data/notepad-database}") String databaseDir) {
        filePath = Paths.get(databaseDir, "bookings.txt");
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        createFileIfMissing();
    }

    private void createFileIfMissing() {
        try {
            if (filePath.getParent() != null) Files.createDirectories(filePath.getParent());
            if (!Files.exists(filePath)) Files.writeString(filePath, "[]");
        } catch (IOException e) {
            throw new RuntimeException("Could not create database file.", e);
        }
    }

    public List<Booking> findAll() {
        try {
            if (!Files.exists(filePath) || Files.size(filePath) == 0) return new ArrayList<>();
            List<Booking> items = mapper.readValue(filePath.toFile(), new TypeReference<List<Booking>>() {});
            return items == null ? new ArrayList<>() : items;
        } catch (IOException e) {
            throw new RuntimeException("Could not read bookings.txt", e);
        }
    }

    public Booking findById(Long id) {
        if (id == null) return null;
        for (Booking item : findAll()) {
            if (id.equals(item.getId())) return item;
        }
        return null;
    }

    public synchronized Booking save(Booking booking) {
        List<Booking> items = findAll();

        if (booking.getId() == null || booking.getId() <= 0) {
            long nextId = 1;
            for (Booking item : items) {
                if (item.getId() != null && item.getId() >= nextId)
                    nextId = item.getId() + 1;
            }
            booking.setId(nextId);
            items.add(booking);
        } else {
            // adding update operation
            boolean found = false;
            for (int i = 0; i < items.size(); i++) {
                if (items.get(i).getId().equals(booking.getId())) {
                    items.set(i, booking);
                    found = true;
                    break;
                }
            }
            if (!found) {
                throw new IllegalArgumentException("Cannot update. Booking not found with ID: " + booking.getId());
            }
        }

        writeAll(items);
        return booking;
    }
    //Adding Delete Opearation
    public synchronized boolean deleteById(Long id) {
        List<Booking> items = findAll();
        boolean removed = items.removeIf(item -> item.getId().equals(id));
        if (removed) {
            writeAll(items);
        }
        return removed;
    }



    public boolean existsById(Long id) { return findById(id) != null; }
    public long count() { return findAll().size(); }

    private void writeAll(List<Booking> items) {
        try {
            mapper.writeValue(filePath.toFile(), items);
        } catch (IOException e) {
            throw new RuntimeException("Could not write bookings.txt", e);
        }
    }

    public List<Booking> findByUserId(Long userId) {
        List<Booking> result = new ArrayList<>();
        for (Booking booking : findAll()) if (userId != null && userId.equals(booking.getUserId())) result.add(booking);
        return result;
    }

}
