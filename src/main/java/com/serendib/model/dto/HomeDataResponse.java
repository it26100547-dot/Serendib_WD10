package com.serendib.model.dto;

import com.serendib.model.Destination;
import com.serendib.model.Hotel;
import com.serendib.model.TourPackage;
import com.serendib.model.Transport;
import java.util.ArrayList;
import java.util.List;

/** Data needed by the four dynamic catalogue sections on the home page. */
public class HomeDataResponse {
    private List<TourPackage> featuredPackages = new ArrayList<>();
    private List<Destination> popularDestinations = new ArrayList<>();
    private List<Transport> availableTransport = new ArrayList<>();
    private List<Hotel> featuredHotels = new ArrayList<>();

    public HomeDataResponse() {}
    public List<TourPackage> getFeaturedPackages() { return featuredPackages; }
    public void setFeaturedPackages(List<TourPackage> featuredPackages) { this.featuredPackages = featuredPackages; }
    public List<Destination> getPopularDestinations() { return popularDestinations; }
    public void setPopularDestinations(List<Destination> popularDestinations) { this.popularDestinations = popularDestinations; }
    public List<Transport> getAvailableTransport() { return availableTransport; }
    public void setAvailableTransport(List<Transport> availableTransport) { this.availableTransport = availableTransport; }
    public List<Hotel> getFeaturedHotels() { return featuredHotels; }
    public void setFeaturedHotels(List<Hotel> featuredHotels) { this.featuredHotels = featuredHotels; }
}
