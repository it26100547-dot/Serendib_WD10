package com.serendib.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Transport extends CatalogItem {
    private static final long serialVersionUID = 1L;

    private String type;
    private Integer capacity;
    private Double price;
    private Boolean driver = true;
    private Boolean ac = true;
    private String status = "Available";
    private List<String> images = new ArrayList<>();
    private List<String> features = new ArrayList<>();
    private List<String> pickupAreas = new ArrayList<>();
    private String terms;

    public Transport() { super(); }

    public Transport(Long id, String name, String type, Integer capacity, Double price, Boolean driver, Boolean ac, String status, String image, String description) {
        super(id, name, type, description, description, image, false);
        this.type = type; this.capacity = capacity; this.price = price; this.driver = driver; this.ac = ac; this.status = status;
    }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; setCategory(type); }
    public Integer getCapacity() { return capacity != null ? capacity : 1; }
    public void setCapacity(Integer capacity) {
        if (capacity != null && capacity < 1) throw new IllegalArgumentException("Vehicle capacity must be at least 1 person.");
        this.capacity = capacity;
    }
    public Double getPrice() { return price != null ? price : 0.0; }
    public void setPrice(Double price) {
        if (price != null && price < 0) throw new IllegalArgumentException("Rental price cannot be negative.");
        this.price = price;
    }
    public Boolean getDriver() { return driver != null ? driver : true; }
    public void setDriver(Boolean driver) { this.driver = driver; }
    public Boolean getAc() { return ac != null ? ac : true; }
    public void setAc(Boolean ac) { this.ac = ac; }
    public String getStatus() { return status != null ? status : "Available"; }
    public void setStatus(String status) { this.status = status; }
    public List<String> getImages() { return images != null ? images : new ArrayList<>(); }
    public void setImages(List<String> images) { this.images = images != null ? images : new ArrayList<>(); }
    public List<String> getFeatures() { return features != null ? features : new ArrayList<>(); }
    public void setFeatures(List<String> features) { this.features = features != null ? features : new ArrayList<>(); }
    public List<String> getPickupAreas() { return pickupAreas != null ? pickupAreas : new ArrayList<>(); }
    public void setPickupAreas(List<String> pickupAreas) { this.pickupAreas = pickupAreas != null ? pickupAreas : new ArrayList<>(); }
    public String getTerms() { return terms; }
    public void setTerms(String terms) { this.terms = terms; }

    public double calculateTotalCost(int rentalDays) { return getPrice() * Math.max(1, rentalDays); }
    public boolean isVehicleAvailable() { return "Available".equalsIgnoreCase(getStatus()); }

    @Override public String getItemType() { return "TRANSPORT"; }
    @Override public String getCardSubtitle() { return getCapacity() + " passengers • " + (getAc() ? "A/C" : "Non-A/C") + (getDriver() ? " • Driver included" : ""); }
    @Override public String getCardPriceFormatted() { return "$" + Math.round(getPrice()) + " / day"; }
}
