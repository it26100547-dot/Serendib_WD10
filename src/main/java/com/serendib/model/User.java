package com.serendib.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * User - Entity representing tourists and administrators.
 * Satisfies Requirement 6:
 * "Sign in page : We need to sign up and sign in ,edit password ."
 *
 * Demonstrates:
 * - INHERITANCE: Extends BaseEntity
 * - ENCAPSULATION: Password protection, validation, and role handling
 */
public class User extends BaseEntity {

    private static final long serialVersionUID = 1L;

    // Encapsulated user fields
    private String firstName;
    private String lastName;
    private String email;

    // Stored in the local Notepad database. REST controllers return safe DTO/maps
    // and never expose this value in HTTP responses.
    private String password;

    private String phone;
    private String country = "Sri Lanka";
    private String role = "TRAVELER"; // ADMIN or TRAVELER

    public User() {
        super();
    }

    public User(Long id, String firstName, String lastName, String email, String password, String phone, String country, String role) {
        super(id);
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.phone = phone;
        this.country = country != null ? country : "Sri Lanka";
        this.role = role != null ? role.toUpperCase() : "TRAVELER";
    }

    // Encapsulated Getters & Setters
    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        if (email != null) {
            this.email = email.trim().toLowerCase();
        }
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getRole() {
        return role != null ? role : "TRAVELER";
    }

    public void setRole(String role) {
        this.role = role != null ? role.toUpperCase() : "TRAVELER";
    }

    // Encapsulated domain business methods
    @JsonIgnore
    public String getFullName() {
        String f = firstName != null ? firstName : "";
        String l = lastName != null ? lastName : "";
        return (f + " " + l).trim();
    }

    @JsonIgnore
    public boolean isAdmin() {
        return "ADMIN".equalsIgnoreCase(this.role);
    }

    /**
     * Encapsulated password verification
     */
    public boolean verifyPassword(String rawPassword) {
        if (this.password == null || rawPassword == null) return false;
        return this.password.equals(rawPassword.trim());
    }

    /**
     * Encapsulated change password operation with validation
     */
    public boolean changePassword(String currentPassword, String newPassword) {
        if (!verifyPassword(currentPassword)) {
            return false;
        }
        if (newPassword == null || newPassword.trim().length() < 4) {
            throw new IllegalArgumentException("New password must be at least 4 characters long.");
        }
        this.password = newPassword.trim();
        markUpdated();
        return true;
    }
}
