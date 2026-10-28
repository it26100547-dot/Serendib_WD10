package com.serendib.service;

import com.serendib.model.User;
import com.serendib.model.dto.ChangePasswordRequest;
import com.serendib.model.dto.RegisterRequest;
import com.serendib.repository.UserRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class UserService {
    private final UserRepository repository;
    public UserService(UserRepository repository) { this.repository = repository; }

    public List<User> getAll() { return repository.findAll(); }
    public User getById(Long id) { return repository.findById(id); }
    public User findByEmail(String email) { return repository.findByEmail(email); }
    public long count() { return repository.count(); }

    public User create(User user) {
        user.setId(null); validate(user);
        if (repository.existsByEmail(user.getEmail())) throw new IllegalArgumentException("Email already exists.");
        return repository.save(user);
    }





    public User register(RegisterRequest request) {
        if (request == null || request.getEmail() == null || request.getEmail().isBlank()) throw new IllegalArgumentException("Email is required.");
        if (request.getPassword() == null || request.getPassword().length() < 4) throw new IllegalArgumentException("Password must be at least 4 characters.");
        if (repository.existsByEmail(request.getEmail())) throw new IllegalArgumentException("An account with this email already exists.");

        User user = new User();
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setEmail(request.getEmail());
        user.setPassword(request.getPassword());
        user.setPhone(request.getPhone());
        user.setCountry(request.getCountry() == null ? "Sri Lanka" : request.getCountry());
        user.setRole("TRAVELER");
        user.setActive(true);
        return repository.save(user);
    }

    public User authenticate(String email, String password) {
        User user = repository.findByEmail(email);
        if (user != null && Boolean.TRUE.equals(user.getActive()) && user.verifyPassword(password)) return user;
        return null;
    }



    private void validate(User user) {
        if (user.getEmail() == null || user.getEmail().isBlank()) throw new IllegalArgumentException("Email is required.");
        if (user.getPassword() == null || user.getPassword().isBlank()) throw new IllegalArgumentException("Password is required.");
    }
}
