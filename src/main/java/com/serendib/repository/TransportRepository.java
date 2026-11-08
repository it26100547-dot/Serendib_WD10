package com.serendib.repository;

import com.serendib.model.Transport;
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
public class TransportRepository {
    private final Path filePath;
    private final ObjectMapper mapper = new ObjectMapper();

    public TransportRepository(@Value("${serendib.database.dir:data/notepad-database}") String databaseDir) {

        filePath = Paths.get(databaseDir, "transports.txt");

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

    public List<Transport> findAll() {
        try {
            if (!Files.exists(filePath) || Files.size(filePath) == 0)
                return new ArrayList<>();
            List<Transport> items = mapper.readValue(filePath.toFile(), new TypeReference<List<Transport>>() {});

            return items == null ? new ArrayList<>() : items;
        } catch (IOException e) {
            throw new RuntimeException("Could not read transports.txt", e);
        }
    }

    public Transport findById(Long id) {

        if (id == null)
            return null;
        for (Transport item : findAll()) {
            if (id.equals(item.getId()))
                return item;
        }
        return null;
    }

    public synchronized Transport save(Transport transport) {
        List<Transport> items = findAll();

        if (transport.getId() == null || transport.getId() <= 0) {

            long nextId = 1;

            for (Transport item : items) {

                if (item.getId() != null && item.getId() >= nextId) nextId = item.getId() + 1;

            }
            transport.setId(nextId);
            items.add(transport);

        } else {
           //adding update operation

            boolean found = false;

            for (int i = 0; i < items.size(); i++) {
                if (transport.getId().equals(items.get(i).getId())) {
                    items.set(i, transport);
                    found = true;
                    break;
                }
            }
            if (!found) {
                items.add(transport);
            }
        }


        writeAll(items);
        return transport;
    }

    // Adding delete operation
    public synchronized boolean deleteById(Long id) {

        if (id == null) return false;
        List<Transport> items = findAll();
        boolean removed = items.removeIf(item -> id.equals(item.getId()));
        if (removed) {
            writeAll(items);
        }
        return removed;
    }


    public boolean existsById(Long id) {
        return findById(id) != null;
    }
    public long count() {
        return findAll().size();
    }

    private void writeAll(List<Transport> items) {

        try {
            mapper.writeValue(filePath.toFile(), items);

        } catch (IOException e) {

            throw new RuntimeException("Could not write transports.txt", e);

        }
    }

    public List<Transport> findByType(String type) {

        if (type == null || type.isBlank())
            return findAll();

        List<Transport> result = new ArrayList<>();

        for (Transport item : findAll())
            if (type.equalsIgnoreCase(item.getType()))
                result.add(item);
        return result;
    }

    public List<Transport> findAvailable() {

        List<Transport> result = new ArrayList<>();
        for (Transport item : findAll())
            if (item.isVehicleAvailable())
                result.add(item);
        return result;
    }

}
