package com.serendib.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Hotel extends CatalogItem {
    private static final long serialVersionUID = 1L;

    private String destination;
    private Double rating = 4.5;
    private Integer stars = 4;
    private Double fromPrice = 50.0;
    private List<Room> rooms = new ArrayList<>();
    private List<String> images = new ArrayList<>();
    private List<String> amenities = new ArrayList<>();
    private String address;
    private String checkIn = "14:00";
    private String checkOut = "11:00";
    private String cancellationPolicy;
    private String contactPhone;

    public Hotel() { super(); }

    public Hotel(Long id, String name, String destination, Double rating, Integer stars, Double fromPrice, String image, String description) {
        super(id, name, "Hotel", description, description, image, false);
        this.destination = destination;
        this.rating = rating;
        this.stars = stars;
        this.fromPrice = fromPrice;
    }

    public String getDestination() { return destination; }
    public void setDestination(String destination) { this.destination = destination; }
    public Double getRating() { return rating != null ? rating : 4.5; }
    public void setRating(Double rating) { this.rating = rating; }
    public Integer getStars() { return stars != null ? stars : 4; }
    public void setStars(Integer stars) {
        if (stars != null) this.stars = Math.max(1, Math.min(5, stars));
    }
    public Double getFromPrice() { return fromPrice != null ? fromPrice : 0.0; }
    public void setFromPrice(Double fromPrice) { this.fromPrice = fromPrice == null ? 0.0 : Math.max(0, fromPrice); }
    // Compatibility with older frontend data field.
    public Double getFrom() { return getFromPrice(); }
    public void setFrom(Double from) { setFromPrice(from); }
    public List<Room> getRooms() { return rooms != null ? rooms : new ArrayList<>(); }
    public void setRooms(List<Room> rooms) { this.rooms = rooms != null ? rooms : new ArrayList<>(); recalculateStartingPrice(); }
    public List<String> getImages() { return images != null ? images : new ArrayList<>(); }
    public void setImages(List<String> images) { this.images = images != null ? images : new ArrayList<>(); }
    public List<String> getAmenities() { return amenities != null ? amenities : new ArrayList<>(); }
    public void setAmenities(List<String> amenities) { this.amenities = amenities != null ? amenities : new ArrayList<>(); }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public String getCheckIn() { return checkIn; }
    public void setCheckIn(String checkIn) { this.checkIn = checkIn; }
    public String getCheckOut() { return checkOut; }
    public void setCheckOut(String checkOut) { this.checkOut = checkOut; }
    public String getCancellationPolicy() { return cancellationPolicy; }
    public void setCancellationPolicy(String cancellationPolicy) { this.cancellationPolicy = cancellationPolicy; }
    public String getContactPhone() { return contactPhone; }
    public void setContactPhone(String contactPhone) { this.contactPhone = contactPhone; }

    public void addRoom(String roomName, int capacity, double price) {
        getRooms().add(new Room(roomName, capacity, price));
        recalculateStartingPrice();
    }

    public void recalculateStartingPrice() {
        if (rooms != null && !rooms.isEmpty()) {
            double min = Double.MAX_VALUE;
            for (Room r : rooms) {
                if (r.getPrice() != null && r.getPrice() < min) min = r.getPrice();
            }
            if (min != Double.MAX_VALUE) this.fromPrice = min;
        }
    }

    @Override public String getItemType() { return "HOTEL"; }
    @Override public String getCardSubtitle() { return "★ " + getRating() + " • " + (destination != null ? destination : "Sri Lanka"); }
    @Override public String getCardPriceFormatted() { return "From $" + Math.round(getFromPrice()); }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Room implements Serializable {
        private static final long serialVersionUID = 1L;
        private String name;
        private Integer capacity;
        private Double price;
        private String description;
        private String image;

        public Room() {}
        public Room(String name, Integer capacity, Double price) { this.name = name; this.capacity = capacity; this.price = price; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public Integer getCapacity() { return capacity; }
        public void setCapacity(Integer capacity) { this.capacity = capacity; }
        public Double getPrice() { return price; }
        public void setPrice(Double price) { this.price = price; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public String getImage() { return image; }
        public void setImage(String image) { this.image = image; }
    }
}
