package com.serendib.repository;

import com.serendib.model.Hotel;
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

/** Reads and writes hotels.txt. Every CRUD step is visible in this class. */
@Repository
public class HotelRepository {
    private final Path filePath;
    private final ObjectMapper mapper = new ObjectMapper();

    public HotelRepository(@Value("${serendib.database.dir:data/notepad-database}") String databaseDir) {
        filePath = Paths.get(databaseDir, "hotels.txt");
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

    public List<Hotel> findAll() {
        try {
            if (!Files.exists(filePath) || Files.size(filePath) == 0) return new ArrayList<>();
            List<Hotel> items = mapper.readValue(filePath.toFile(), new TypeReference<List<Hotel>>() {});
            return items == null ? new ArrayList<>() : items;
        } catch (IOException e) {
            throw new RuntimeException("Could not read hotels.txt", e);
        }
    }

    public Hotel findById(Long id) {
        if (id == null) return null;
        for (Hotel item : findAll()) {
            if (id.equals(item.getId())) return item;
        }
        return null;
    }

    public synchronized Hotel save(Hotel hotel) {
        List<Hotel> items = findAll();

        if (hotel.getId() == null || hotel.getId() <= 0) {
            long nextId = 1;
            for (Hotel item : items) {
                if (item.getId() != null && item.getId() >= nextId) nextId = item.getId() + 1;
            }
            hotel.setId(nextId);
            items.add(hotel);
        } else {
            throw new IllegalArgumentException("Updating existing records is not implemented yet.");
        }

        writeAll(items);
        return hotel;
    }



    public boolean existsById(Long id) { return findById(id) != null; }
    public long count() { return findAll().size(); }

    private void writeAll(List<Hotel> items) {
        try {
            mapper.writeValue(filePath.toFile(), items);
        } catch (IOException e) {
            throw new RuntimeException("Could not write hotels.txt", e);
        }
    }

    public List<Hotel> findByDestination(String destination) {
        if (destination == null || destination.isBlank()) return findAll();
        List<Hotel> result = new ArrayList<>();
        for (Hotel item : findAll()) if (destination.equalsIgnoreCase(item.getDestination())) result.add(item);
        return result;
    }

    public List<Hotel> findByMaxPrice(double maxPrice) {
        List<Hotel> result = new ArrayList<>();
        for (Hotel item : findAll()) if (item.getFromPrice() <= maxPrice) result.add(item);
        return result;
    }

}
