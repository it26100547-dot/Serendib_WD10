package com.serendib.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * BaseEntity - Abstract parent entity demonstrating INHERITANCE and ENCAPSULATION.
 * All domain models in Serendib inherit core metadata and lifecycle fields from this class.
 */
public abstract class BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    // Private fields enforcing ENCAPSULATION
    private Long id;
    private Boolean active = true;
    private String createdAt;
    private String updatedAt;

    public BaseEntity() {
        this.createdAt = LocalDateTime.now().format(FORMATTER);
        this.updatedAt = this.createdAt;
    }

    public BaseEntity(Long id) {
        this();
        this.id = id;
    }

    // Encapsulated Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Boolean getActive() {
        return active != null ? active : true;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    // Encapsulated domain business method
    public void markUpdated() {
        this.updatedAt = LocalDateTime.now().format(FORMATTER);
    }
}
