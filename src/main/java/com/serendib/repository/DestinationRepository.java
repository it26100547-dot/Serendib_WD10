package com.serendib.repository;

import com.serendib.model.Destination;
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
public class DestinationRepository {
    private final Path filePath;
    private final ObjectMapper mapper = new ObjectMapper();

    public DestinationRepository(@Value("${serendib.database.dir:data/notepad-database}") String databaseDir) {
        filePath = Paths.get(databaseDir, "destinations.txt");
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

    public List<Destination> findAll() {
        try {
            if (!Files.exists(filePath) || Files.size(filePath) == 0) return new ArrayList<>();
            List<Destination> items = mapper.readValue(filePath.toFile(), new TypeReference<List<Destination>>() {});
            return items == null ? new ArrayList<>() : items;
        } catch (IOException e) {
            throw new RuntimeException("Could not read destinations.txt", e);
        }
    }

    public Destination findById(Long id) {
        if (id == null) return null;
        for (Destination item : findAll()) {
            if (id.equals(item.getId())) return item;
        }
        return null;
    }

    public synchronized Destination save(Destination destination) {
        List<Destination> items = findAll();

        if (destination.getId() == null || destination.getId() <= 0) {
            long nextId = 1;
            for (Destination item : items) {
                if (item.getId() != null && item.getId() >= nextId) nextId = item.getId() + 1;
            }
            destination.setId(nextId);
            items.add(destination);
        } else {
            // adding update operation
            boolean updated = false;
            for (int i = 0; i < items.size(); i++) {
                if (destination.getId().equals(items.get(i).getId())) {
                    items.set(i, destination);
                    updated = true;
                    break;
                }
            }

            if (!updated) {
                throw new IllegalArgumentException("Destination with ID " + destination.getId() + " not found.");
            }
        }

        writeAll(items);
        return destination;
    }



    public boolean existsById(Long id) { return findById(id) != null; }
    public long count() { return findAll().size(); }

    private void writeAll(List<Destination> items) {
        try {
            mapper.writeValue(filePath.toFile(), items);
        } catch (IOException e) {
            throw new RuntimeException("Could not write destinations.txt", e);
        }
    }

    public List<Destination> findByRegion(String region) {
        if (region == null || region.isBlank()) return findAll();
        List<Destination> result = new ArrayList<>();
        for (Destination item : findAll()) if (region.equalsIgnoreCase(item.getRegion())) result.add(item);
        return result;
    }

    public List<Destination> findFeatured() {
        List<Destination> result = new ArrayList<>();
        for (Destination item : findAll()) if (Boolean.TRUE.equals(item.getFeatured())) result.add(item);
        return result;
    }

}
