package com.serendib.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TourPackage extends CatalogItem {

    private static final long serialVersionUID = 1L;


    private Integer days = 7;
    private Integer nights = 6;
    private Double normalPrice = 950.0;
    private Double price = 790.0;
    private Double rating = 4.9;
    private String cover;
    private List<String> images = new ArrayList<>();
    private List<String> destinations = new ArrayList<>();
    private List<Long> destinationIds = new ArrayList<>();


    private Long transportId;
    private String transportName;
    private Long hotelId;
    private String hotelName;


    private List<List<String>> itinerary = new ArrayList<>();


    private String route;
    private String priceUnit = "per person";
    private List<String> highlights = new ArrayList<>();
    private List<String> inclusions = new ArrayList<>();
    private List<String> exclusions = new ArrayList<>();
    private String meetingPoint;
    private String cancellationPolicy;
    private String bookingNote;

    public TourPackage() {

        super();

    }

    public TourPackage(Long id, String name, String category, Integer days, Integer nights, Double normalPrice, Double price, Double rating, Boolean featured, String cover, String summary, String description) {
        super(id, name, category, summary, description, cover, featured);
        this.days = days;
        this.nights = nights;
        this.normalPrice = normalPrice;
        this.price = price;
        this.rating = rating;
        this.cover = cover;
    }


    public Integer getDays() {

        return days != null ? days : 1;

    }

    public void setDays(Integer days) {

        if (days != null && days < 1) {

            throw new IllegalArgumentException("Tour duration must be at least 1 day.");

        }

        this.days = days;
    }

    public Integer getNights() {
        return nights != null ? nights : 0;
    }

    public void setNights(Integer nights) {
        this.nights = nights;
    }

    public Double getNormalPrice() {
        return normalPrice != null ? normalPrice : getPrice();
    }

    public void setNormalPrice(Double normalPrice) {
        this.normalPrice = normalPrice;
    }

    public Double getPrice() {
        return price != null ? price : 0.0;
    }

    public void setPrice(Double price) {

        if (price != null && price < 0) {

            throw new IllegalArgumentException("Package price cannot be negative.");

        }

        this.price = price;

    }

    public Double getRating() {
        return rating != null ? rating : 5.0;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }

    public String getCover() {
        return cover != null ? cover : getImage();
    }

    public void setCover(String cover) {
        this.cover = cover;
        setImage(cover);
    }

    public List<String> getImages() {
        return images != null ? images : new ArrayList<>();
    }

    public void setImages(List<String> images) {
        this.images = images != null ? images : new ArrayList<>();
    }

    public List<String> getDestinations() {
        return destinations != null ? destinations : new ArrayList<>();
    }

    public void setDestinations(List<String> destinations) {
        this.destinations = destinations != null ? destinations : new ArrayList<>();
    }

    public List<Long> getDestinationIds() {
        return destinationIds != null ? destinationIds : new ArrayList<>();
    }

    public void setDestinationIds(List<Long> destinationIds) {
        this.destinationIds = destinationIds != null ? destinationIds : new ArrayList<>();
    }

    public Long getTransportId() {
        return transportId;
    }

    public void setTransportId(Long transportId) {
        this.transportId = transportId;
    }

    public String getTransportName() {
        return transportName;
    }

    public void setTransportName(String transportName) {
        this.transportName = transportName;
    }

    public Long getHotelId() {
        return hotelId;
    }

    public void setHotelId(Long hotelId) {
        this.hotelId = hotelId;
    }

    public String getHotelName() {
        return hotelName;
    }

    public void setHotelName(String hotelName) {
        this.hotelName = hotelName;
    }


    public List<List<String>> getItinerary() {
        return itinerary != null ? itinerary : new ArrayList<>();
    }

    public void setItinerary(List<List<String>> itinerary) {
        this.itinerary = itinerary != null ? itinerary : new ArrayList<>();
    }

    public String getRoute() {
        return route;
    }
    public void setRoute(String route) {
        this.route = route;
    }

    public String getPriceUnit() {
        return priceUnit != null ? priceUnit : "per person";
    }
    public void setPriceUnit(String priceUnit) {
        this.priceUnit = priceUnit;
    }

    public List<String> getHighlights() {
        return highlights != null ? highlights : new ArrayList<>();
    }
    public void setHighlights(List<String> highlights) {
        this.highlights = highlights != null ? highlights : new ArrayList<>();
    }

    public List<String> getInclusions() {
        return inclusions != null ? inclusions : new ArrayList<>();
    }
    public void setInclusions(List<String> inclusions) {
        this.inclusions = inclusions != null ? inclusions : new ArrayList<>();
    }

    public List<String> getExclusions() {
        return exclusions != null ? exclusions : new ArrayList<>();
    }
    public void setExclusions(List<String> exclusions) {
        this.exclusions = exclusions != null ? exclusions : new ArrayList<>();
    }

    public String getMeetingPoint() {
        return meetingPoint;
    }
    public void setMeetingPoint(String meetingPoint) {
        this.meetingPoint = meetingPoint;
    }

    public String getCancellationPolicy() {
        return cancellationPolicy;
    }
    public void setCancellationPolicy(String cancellationPolicy) {
        this.cancellationPolicy = cancellationPolicy;
    }

    public String getBookingNote() {
        return bookingNote;
    }
    public void setBookingNote(String bookingNote) {
        this.bookingNote = bookingNote;
    }


    public double calculateSavings() {
        double regular = getNormalPrice();
        double discounted = getPrice();
        return Math.max(0.0, regular - discounted);
    }

    public int calculateDiscountPercentage() {
        if (getNormalPrice() <= 0) return 0;
        double savings = calculateSavings();
        return (int) Math.round((savings / getNormalPrice()) * 100);
    }


    @Override
    public String getItemType() {
        return "PACKAGE";
    }

    @Override
    public String getCardSubtitle() {
        int stops = getDestinations().size();
        return getDays() + " Days / " + getNights() + " Nights • " + stops + " Stops";
    }

    @Override
    public String getCardPriceFormatted() {
        return "$" + Math.round(getPrice());
    }

    @Override
    public Map<String, Object> toCardMap() {
        Map<String, Object> map = super.toCardMap();
        map.put("days", getDays());
        map.put("nights", getNights());
        map.put("rating", getRating());
        map.put("normalPrice", "$" + Math.round(getNormalPrice()));
        map.put("savings", "$" + Math.round(calculateSavings()));
        map.put("destinations", getDestinations());
        map.put("transportName", getTransportName());
        map.put("hotelName", getHotelName());
        return map;
    }
}
