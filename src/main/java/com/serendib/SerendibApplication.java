package com.serendib;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/** The main method that starts the Spring Boot web server. */
@SpringBootApplication
public class SerendibApplication {
    public static void main(String[] args) {
        SpringApplication.run(SerendibApplication.class, args);
        System.out.println("Serendib is running at http://localhost:8080");
    }
}
