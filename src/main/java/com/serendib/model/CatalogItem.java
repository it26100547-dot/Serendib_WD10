package com.serendib.model;

import java.util.LinkedHashMap;
import java.util.Map;


public abstract class CatalogItem extends BaseEntity implements DisplayableCard {

    private static final long serialVersionUID = 1L;


    private String name;
    private String category;
    private String summary;
    private String description;
    private String image;
    private Boolean featured = false;

    public CatalogItem() {

        super();

    }

    public CatalogItem(Long id, String name, String category, String summary, String description, String image, Boolean featured) {

        super(id);
        this.name = name;
        this.category = category;
        this.summary = summary;
        this.description = description;
        this.image = image;
        this.featured = featured;

    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public Boolean getFeatured() {
        return featured != null ? featured : false;
    }

    public void setFeatured(Boolean featured) {
        this.featured = featured;
    }


    @Override
    public String getCardTitle() {
        return this.name != null ? this.name : "";
    }

    @Override
    public String getCardCategory() {
        return this.category != null ? this.category : "General";
    }

    @Override
    public String getCardImage() {
        return this.image != null ? this.image : "";
    }

    @Override
    public Map<String, Object> toCardMap() {

        Map<String, Object> map = new LinkedHashMap<>();
        map.put("id", getId());
        map.put("type", getItemType());
        map.put("title", getCardTitle());
        map.put("category", getCardCategory());
        map.put("subtitle", getCardSubtitle());
        map.put("price", getCardPriceFormatted());
        map.put("image", getCardImage());
        map.put("summary", getSummary());
        map.put("featured", getFeatured());
        return map;

    }
}
