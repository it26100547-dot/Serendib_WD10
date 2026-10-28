package com.serendib;

import com.serendib.model.*;
import com.serendib.repository.*;
import com.serendib.service.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class SerendibApplicationTests {

    @Autowired
    private DestinationService destinationService;

    @Autowired
    private TransportService transportService;

    @Autowired
    private HotelService hotelService;


    @Autowired
    private TourPackageService tourPackageService;

    @Autowired
    private UserService userService;

    @Autowired
    private HomeService homeService;

    @Test
    @DisplayName("Context Loads & Spring Boot starts properly")
    void contextLoads() {
        assertNotNull(destinationService);
        assertNotNull(tourPackageService);
        assertNotNull(homeService);
    }

    @Test
    @DisplayName("OOP Concept 1 - Encapsulation: Testing User password verification and change")
    void testEncapsulation() {
        User user = new User();
        user.setEmail("test@travel.com");
        user.setPassword("secret123");

        // Verify password encapsulation
        assertTrue(user.verifyPassword("secret123"));
        assertFalse(user.verifyPassword("wrongpass"));

        // Change password encapsulation
        boolean changed = user.changePassword("secret123", "newsecret456");
        assertTrue(changed);
        assertTrue(user.verifyPassword("newsecret456"));
    }

    @Test
    @DisplayName("OOP Concept 2 - Inheritance: CatalogItem hierarchy")
    void testInheritance() {
        Destination dest = new Destination();
        dest.setName("Sigiriya");

        // Inherited methods from BaseEntity
        assertNotNull(dest.getCreatedAt());
        assertTrue(dest.getActive());

        // Inherited methods from CatalogItem
        dest.setCategory("Heritage");
        assertEquals("Heritage", dest.getCategory());
        assertTrue(dest instanceof CatalogItem);
        assertTrue(dest instanceof BaseEntity);
    }

    @Test
    @DisplayName("OOP Concept 3 - Polymorphism: DisplayableCard interface")
    void testPolymorphism() {
        // Diverse catalog items treated polymorphically
        DisplayableCard destCard = new Destination(1L, "Kandy", "Central", "Culture", "img.jpg", "Summary", List.of("Tooth Relic"));
        DisplayableCard pkgCard = new TourPackage(1L, "Secret Udrata", "Hill Country", 7, 6, 950.0, 790.0, 4.9, true, "img.jpg", "Sum", "Desc");
        DisplayableCard vehicleCard = new Transport(1L, "Toyota KDH", "Van", 10, 120.0, true, true, "Available", "img.jpg", "Desc");

        assertEquals("DESTINATION", destCard.getItemType());
        assertEquals("PACKAGE", pkgCard.getItemType());
        assertEquals("TRANSPORT", vehicleCard.getItemType());

        // Polymorphic card map generation
        assertNotNull(destCard.toCardMap().get("title"));
        assertNotNull(pkgCard.toCardMap().get("price"));
        assertNotNull(vehicleCard.toCardMap().get("subtitle"));
    }

    @Test
    @DisplayName("Requirement 5 - Tour Packages all-in-one combination")
    void testTourPackageCombination() {
        List<TourPackage> packages = tourPackageService.getAll();
        assertFalse(packages.isEmpty(), "Packages should be loaded from Notepad NoSQL database");

        TourPackage first = packages.get(0);
        assertNotNull(first.getName());
        assertNotNull(first.getTransportName(), "Package should combine transport");
        assertNotNull(first.getHotelName(), "Package should combine hotel");
        assertTrue(first.calculateSavings() > 0, "Package savings calculation should work");
    }

    @Test
    @DisplayName("Requirement 7 - Dynamic Home topics and cards")
    void testDynamicHomeData() {
        var homeData = homeService.getHomePageData();
        assertNotNull(homeData);
        assertFalse(homeData.getFeaturedPackages().isEmpty(), "Featured packages topic should be dynamic");
        assertFalse(homeData.getPopularDestinations().isEmpty(), "Popular destinations topic should be dynamic");
        assertFalse(homeData.getAvailableTransport().isEmpty(), "Transport topic should be dynamic");
        assertFalse(homeData.getFeaturedHotels().isEmpty(), "Hotels topic should be dynamic");
    }
}
