package com.serendib.model;

import java.util.Map;

/**
 * DisplayableCard - Polymorphic interface for all card items displayed on the UI.
 * Enables POLYMORPHISM: Home page and UI card components can process Destinations,
 * Transports, Hotels, and Tour Packages uniformly.
 */
public interface DisplayableCard {

    /**
     * Polymorphic method to get the entity classification.
     */
    String getItemType();

    /**
     * Polymorphic method to get the main title of the card.
     */
    String getCardTitle();

    /**
     * Polymorphic method to get the subtitle or badge info.
     */
    String getCardSubtitle();

    /**
     * Polymorphic method to get the category eyebrow text.
     */
    String getCardCategory();

    /**
     * Polymorphic method to get the hero card image.
     */
    String getCardImage();

    /**
     * Polymorphic method to get the formatted price or value.
     */
    String getCardPriceFormatted();

    /**
     * Polymorphic conversion to a standardized card representation.
     */
    Map<String, Object> toCardMap();
}
