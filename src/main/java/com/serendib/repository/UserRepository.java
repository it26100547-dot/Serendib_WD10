package com.serendib.repository;

import com.serendib.model.User;
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

/** Reads and writes users.txt. Every CRUD step is visible in this class. */
@Repository
public class UserRepository {
    private final Path filePath;
    private final ObjectMapper mapper = new ObjectMapper();

    public UserRepository(@Value("${serendib.database.dir:data/notepad-database}") String databaseDir) {
        filePath = Paths.get(databaseDir, "users.txt");
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

    public List<User> findAll() {
        try {
            if (!Files.exists(filePath) || Files.size(filePath) == 0) return new ArrayList<>();
            List<User> items = mapper.readValue(filePath.toFile(), new TypeReference<List<User>>() {});
            return items == null ? new ArrayList<>() : items;
        } catch (IOException e) {
            throw new RuntimeException("Could not read users.txt", e);
        }
    }

    public User findById(Long id) {
        if (id == null) return null;
        for (User item : findAll()) {
            if (id.equals(item.getId())) return item;
        }
        return null;
    }

    public synchronized User save(User user) {
        List<User> items = findAll();

        if (user.getId() == null || user.getId() <= 0) {
            long nextId = 1;
            for (User item : items) {
                if (item.getId() != null && item.getId() >= nextId) nextId = item.getId() + 1;
            }
            user.setId(nextId);
            items.add(user);
        } else {
            // Logic for UPDATE: replace the existing record in the list
            boolean found = false;
            for (int i = 0; i < items.size(); i++) {
                if (user.getId().equals(items.get(i).getId())) {
                    items.set(i, user);
                    found = true;
                    break;
                }
            }
            if (!found) {
                items.add(user);
            }
        }

        writeAll(items);
        return user;
    }



    public boolean existsById(Long id) { return findById(id) != null; }
    public long count() { return findAll().size(); }

    private void writeAll(List<User> items) {
        try {
            mapper.writeValue(filePath.toFile(), items);
        } catch (IOException e) {
            throw new RuntimeException("Could not write users.txt", e);
        }
    }

    public User findByEmail(String email) {
        if (email == null) return null;
        for (User user : findAll()) {
            if (user.getEmail() != null && email.equalsIgnoreCase(user.getEmail())) return user;
        }
        return null;
    }

    public boolean existsByEmail(String email) { return findByEmail(email) != null; }

}
