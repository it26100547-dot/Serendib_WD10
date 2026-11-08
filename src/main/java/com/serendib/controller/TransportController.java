package com.serendib.controller;

import com.serendib.model.Transport;
import com.serendib.model.dto.ApiResponse;
import com.serendib.security.SessionService;
import com.serendib.service.TransportService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/transport")
public class TransportController {
    private final TransportService service;
    private final SessionService sessionService;

    public TransportController(TransportService service, SessionService sessionService) {
        this.service = service;
        this.sessionService = sessionService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse> getAll(@RequestParam(name = "type", required = false) String type) {

        List<Transport> list = (type == null || type.isBlank()) ? service.getAll() : service.getByType(type);
        return ResponseEntity.ok(ApiResponse.ok(list));

    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> getById(@PathVariable("id") Long id) {

        Transport item = service.getById(id);

        if (item == null)
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.error("Transport not found."));
        return ResponseEntity.ok(ApiResponse.ok(item));

    }

    @PostMapping
    public ResponseEntity<ApiResponse> create(@RequestBody Transport transport, HttpSession session) {

        if (!sessionService.isAdmin(session))

            return adminOnly(session);

        try {

            Transport saved = service.create(transport);
            return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok("Transport created successfully.", saved));

        } catch (IllegalArgumentException e) {

            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    // Adding update operation
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse> update(@PathVariable("id") Long id, @RequestBody Transport transport, HttpSession session) {

        if (!sessionService.isAdmin(session)) {

            return adminOnly(session);
        }
        try {
            Transport updated = service.update(id, transport);

            return ResponseEntity.ok(ApiResponse.ok("Transport updated successfully.", updated));

        } catch (IllegalArgumentException e) {

            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    //Adding delete operation
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> delete(@PathVariable("id") Long id, HttpSession session) {

        if (!sessionService.isAdmin(session)) {

            return adminOnly(session);
        }
        try {
            service.delete(id);

            return ResponseEntity.ok(ApiResponse.ok("Transport deleted successfully.", null));

        } catch (IllegalArgumentException e) {

            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));

        }
    }


    private ResponseEntity<ApiResponse> adminOnly(HttpSession session) {

        if (sessionService.getCurrentUser(session) == null) {

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ApiResponse.error("Please sign in first."));

        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ApiResponse.error("Administrator permission is required."));
    }
}
