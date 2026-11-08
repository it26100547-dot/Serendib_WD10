package com.serendib.controller;

import com.serendib.model.User;
import com.serendib.model.dto.ApiResponse;
import com.serendib.model.dto.AuthRequest;
import com.serendib.model.dto.ChangePasswordRequest;
import com.serendib.model.dto.RegisterRequest;
import com.serendib.security.SessionService;
import com.serendib.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final UserService userService;
    private final SessionService sessionService;

    public AuthController(UserService userService, SessionService sessionService) {
        this.userService = userService;
        this.sessionService = sessionService;
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse> register(@RequestBody RegisterRequest request) {
        try {

            User user = userService.register(request);

            return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok("Registration successful.", safeUser(user)));

        } catch (IllegalArgumentException e) {

            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse> login(@RequestBody AuthRequest request, HttpSession session) {

        User user = userService.authenticate(request.getEmail(), request.getPassword());

        if (user == null)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ApiResponse.error("Invalid email or password."));

        sessionService.login(session, user);

        return ResponseEntity.ok(ApiResponse.ok("Login successful.", safeUser(user)));
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse> me(HttpSession session) {

        User user = sessionService.getCurrentUser(session);

        if (user == null)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ApiResponse.error("You are not signed in."));

        return ResponseEntity.ok(ApiResponse.ok(safeUser(user)));
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse> logout(HttpSession session) {
        sessionService.logout(session);
        return ResponseEntity.ok(ApiResponse.ok("Signed out successfully.", "Signed out"));
    }




    private Map<String, Object> safeUser(User user) {

        Map<String, Object> data = new LinkedHashMap<>();
        data.put("id", user.getId());
        data.put("firstName", user.getFirstName());
        data.put("lastName", user.getLastName());
        data.put("fullName", user.getFullName());
        data.put("email", user.getEmail());
        data.put("phone", user.getPhone());
        data.put("country", user.getCountry());
        data.put("role", user.getRole());
        data.put("active", user.getActive());
        data.put("admin", user.isAdmin());
        return data;

    }
}
