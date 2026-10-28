package com.serendib.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/** Destination with fully editable detail-page content. */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Destination extends CatalogItem {
    private static final long serialVersionUID = 1L;

    private String region;
    private List<String> attractions = new ArrayList<>();
    private List<String> images = new ArrayList<>();
    private List<Place> places = new ArrayList<>();
    private List<Long> recommendedHotelIds = new ArrayList<>();
    private List<Long> recommendedTransportIds = new ArrayList<>();
    private String bestTime;
    private Integer suggestedDays = 2;
    private String travelTips;

    public Destination() { super(); }

    public Destination(Long id, String name, String region, String category, String image, String summary, List<String> attractions) {
        super(id, name, category, summary, summary, image, false);
        this.region = region;
        this.attractions = attractions != null ? attractions : new ArrayList<>();
    }

    public String getRegion() { return region; }
    public void setRegion(String region) { this.region = region; }
    public List<String> getAttractions() { return attractions != null ? attractions : new ArrayList<>(); }
    public void setAttractions(List<String> attractions) { this.attractions = attractions != null ? attractions : new ArrayList<>(); }
    public List<String> getImages() { return images != null ? images : new ArrayList<>(); }
    public void setImages(List<String> images) { this.images = images != null ? images : new ArrayList<>(); }
    public List<Place> getPlaces() { return places != null ? places : new ArrayList<>(); }
    public void setPlaces(List<Place> places) { this.places = places != null ? places : new ArrayList<>(); }
    public List<Long> getRecommendedHotelIds() { return recommendedHotelIds != null ? recommendedHotelIds : new ArrayList<>(); }
    public void setRecommendedHotelIds(List<Long> ids) { this.recommendedHotelIds = ids != null ? ids : new ArrayList<>(); }
    public List<Long> getRecommendedTransportIds() { return recommendedTransportIds != null ? recommendedTransportIds : new ArrayList<>(); }
    public void setRecommendedTransportIds(List<Long> ids) { this.recommendedTransportIds = ids != null ? ids : new ArrayList<>(); }
    public String getBestTime() { return bestTime; }
    public void setBestTime(String bestTime) { this.bestTime = bestTime; }
    public Integer getSuggestedDays() { return suggestedDays != null ? suggestedDays : 2; }
    public void setSuggestedDays(Integer suggestedDays) { this.suggestedDays = suggestedDays; }
    public String getTravelTips() { return travelTips; }
    public void setTravelTips(String travelTips) { this.travelTips = travelTips; }

    public void addAttraction(String attraction) {
        if (attraction != null && !attraction.trim().isEmpty()) getAttractions().add(attraction.trim());
    }

    public int getAttractionCount() {
        if (!getPlaces().isEmpty()) return getPlaces().size();
        return getAttractions().size();
    }

    @Override public String getItemType() { return "DESTINATION"; }
    @Override public String getCardSubtitle() { return (region != null ? region : "Sri Lanka") + " Region"; }
    @Override public String getCardPriceFormatted() { return getAttractionCount() + " places"; }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Place implements Serializable {
        private static final long serialVersionUID = 1L;
        private String name;
        private String description;
        private String image;
        private String category;

        public Place() {}
        public Place(String name, String description, String image) {
            this.name = name; this.description = description; this.image = image;
        }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public String getImage() { return image; }
        public void setImage(String image) { this.image = image; }
        public String getCategory() { return category; }
        public void setCategory(String category) { this.category = category; }
    }
}
