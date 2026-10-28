package com.serendib.repository;

import com.serendib.model.TourPackage;
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

/** Reads and writes packages.txt. Every CRUD step is visible in this class. */
@Repository
public class TourPackageRepository {
    private final Path filePath;
    private final ObjectMapper mapper = new ObjectMapper();

    public TourPackageRepository(@Value("${serendib.database.dir:data/notepad-database}") String databaseDir) {
        filePath = Paths.get(databaseDir, "packages.txt");
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        createFileIfMissing();
    }

    private void createFileIfMissing() {
        try {
            if (filePath.getParent() != null)
                Files.createDirectories(filePath.getParent());
            if (!Files.exists(filePath))
                Files.writeString(filePath, "[]");
        } catch (IOException e) {
            throw new RuntimeException("Could not create database file.", e);
        }
    }

    public List<TourPackage> findAll() {
        try {
            if (!Files.exists(filePath) || Files.size(filePath) == 0)
                return new ArrayList<>();
            List<TourPackage> items = mapper.readValue(filePath.toFile(), new TypeReference<List<TourPackage>>() {});
            return items == null ? new ArrayList<>() : items;
        } catch (IOException e) {
            throw new RuntimeException("Could not read packages.txt", e);
        }
    }

    public TourPackage findById(Long id) {
        if (id == null) return null;
        for (TourPackage item : findAll()) {
            if (id.equals(item.getId())) return item;
        }
        return null;
    }

    public synchronized TourPackage save(TourPackage tourPackage) {
        List<TourPackage> items = findAll();

        if (tourPackage.getId() == null || tourPackage.getId() <= 0) {
            // Logic for CREATE: Generate a new ID
            long nextId = 1;
            for (TourPackage item : items) {
                if (item.getId() != null && item.getId() >= nextId)
                    nextId = item.getId() + 1;
            }
            tourPackage.setId(nextId);
            items.add(tourPackage);
        } else {
            // Logic for UPDATE: Find existing package and update its fields
            int targetIndex = -1;
            for (int i = 0; i < items.size(); i++) {
                if (tourPackage.getId().equals(items.get(i).getId())) {
                    targetIndex = i;
                    break;
                }
            }

            if (targetIndex != -1) {
                items.set(targetIndex, tourPackage);
            } else {
                throw new IllegalArgumentException("Package with ID " + tourPackage.getId() + " not found.");
            }
        }

        // Save updated list back to file
        writeAll(items);
        return tourPackage;
    }




    public boolean existsById(Long id) {
        return findById(id) != null;
    }
    public long count() {
        return findAll().size();
    }

    private void writeAll(List<TourPackage> items) {
        try {
            mapper.writeValue(filePath.toFile(), items);
        } catch (IOException e) {
            throw new RuntimeException("Could not write packages.txt", e);
        }
    }

    public List<TourPackage> findFeatured() {
        List<TourPackage> result = new ArrayList<>();
        for (TourPackage item : findAll())
            if (Boolean.TRUE.equals(item.getFeatured()))
                result.add(item);
        return result;
    }

    public List<TourPackage> findByCategory(String category) {
        if (category == null || category.isBlank())
            return findAll();
        List<TourPackage> result = new ArrayList<>();
        for (TourPackage item : findAll())
            if (category.equalsIgnoreCase(item.getCategory()))
                result.add(item);
        return result;
    }

    public List<TourPackage> filter(String search, String category, Double maxPrice, Integer maxDays) {
        List<TourPackage> result = new ArrayList<>();
        for (TourPackage p : findAll()) {
            boolean matches = true;
            if (search != null && !search.isBlank()) {
                String text = search.toLowerCase();
                String name = p.getName() == null ? "" : p.getName().toLowerCase();
                String summary = p.getSummary() == null ? "" : p.getSummary().toLowerCase();
                matches = name.contains(text) || summary.contains(text);
            }

            if (matches && category != null && !category.isBlank() && !"All".equalsIgnoreCase(category))
                matches = category.equalsIgnoreCase(p.getCategory());

            if (matches && maxPrice != null && maxPrice > 0)
                matches = p.getPrice() <= maxPrice;

            if (matches && maxDays != null && maxDays > 0)
                matches = p.getDays() <= maxDays;

            if (matches) result.add(p);
        }
        return result;
    }

}
