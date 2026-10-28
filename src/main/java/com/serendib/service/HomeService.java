package com.serendib.service;

import com.serendib.model.Destination;
import com.serendib.model.Hotel;
import com.serendib.model.TourPackage;
import com.serendib.model.Transport;
import com.serendib.model.dto.HomeDataResponse;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/** Builds the dynamic data used on the home page. */
@Service
public class HomeService {
    private final TourPackageService packageService;
    private final DestinationService destinationService;
    private final TransportService transportService;
    private final HotelService hotelService;

    public HomeService(TourPackageService packageService,
                       DestinationService destinationService,
                       TransportService transportService,
                       HotelService hotelService) {
        this.packageService = packageService;
        this.destinationService = destinationService;
        this.transportService = transportService;
        this.hotelService = hotelService;
    }

    public HomeDataResponse getHomePageData() {
        HomeDataResponse response = new HomeDataResponse();

        List<TourPackage> packages = packageService.getFeatured();
        if (packages.isEmpty()) packages = packageService.getAll();
        response.setFeaturedPackages(new ArrayList<>(packages.subList(0, Math.min(6, packages.size()))));

        List<Destination> destinations = destinationService.getAll();
        response.setPopularDestinations(new ArrayList<>(destinations.subList(0, Math.min(4, destinations.size()))));

        List<Transport> transport = transportService.getAll();
        response.setAvailableTransport(new ArrayList<>(transport.subList(0, Math.min(3, transport.size()))));

        List<Hotel> hotels = hotelService.getAll();
        response.setFeaturedHotels(new ArrayList<>(hotels.subList(0, Math.min(3, hotels.size()))));


        return response;
    }
}
