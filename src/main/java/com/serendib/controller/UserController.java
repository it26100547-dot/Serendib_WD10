package com.serendib.controller;

import com.serendib.model.User;
import com.serendib.model.dto.ApiResponse;
import com.serendib.security.SessionService;
import com.serendib.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService service;
    private final SessionService sessionService;
    public UserController(UserService service, SessionService sessionService) {
        this.service = service; this.sessionService = sessionService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse> getAll(HttpSession session) {
        if (!sessionService.isAdmin(session))
            return adminOnly(session);
        List<Map<String, Object>> result = new ArrayList<>();
        for (User user : service.getAll())
            result.add(safeUser(user));
        return ResponseEntity.ok(ApiResponse.ok(result));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> getById(@PathVariable("id") Long id, HttpSession session) {
        if (!sessionService.isAdmin(session))
            return adminOnly(session);
        User user = service.getById(id);
        if (user == null)
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.error("User not found."));
        return ResponseEntity.ok(ApiResponse.ok(safeUser(user)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse> create(@RequestBody User user, HttpSession session) {
        if (!sessionService.isAdmin(session))
            return adminOnly(session);
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok("User created.", safeUser(service.create(user))));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }



    private Map<String, Object> safeUser(User user) {
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("id", user.getId()); data.put("active", user.getActive());
        data.put("createdAt", user.getCreatedAt()); data.put("updatedAt", user.getUpdatedAt());
        data.put("firstName", user.getFirstName()); data.put("lastName", user.getLastName());
        data.put("fullName", user.getFullName()); data.put("email", user.getEmail());
        data.put("phone", user.getPhone()); data.put("country", user.getCountry());
        data.put("role", user.getRole()); data.put("admin", user.isAdmin());
        return data;
    }

    private ResponseEntity<ApiResponse> adminOnly(HttpSession session) {
        if (sessionService.getCurrentUser(session) == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ApiResponse.error("Please sign in first."));
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ApiResponse.error("Administrator permission is required."));
    }
}
