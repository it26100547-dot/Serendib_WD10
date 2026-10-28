package com.serendib.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Paths;

/** Simple web configuration: page routes, CORS and uploaded images. */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    private final String uploadDir;

    public WebMvcConfig(@Value("${serendib.upload.dir:data/uploads}") String uploadDir) {
        this.uploadDir = uploadDir;
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*");
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String location = Paths.get(uploadDir, "images").toAbsolutePath().normalize().toUri().toString();
        if (!location.endsWith("/")) location += "/";
        registry.addResourceHandler("/uploads/images/**").addResourceLocations(location);

        // Serve static assets and videos directly with no-cache revalidation to prevent stale browser caching
        registry.addResourceHandler("/**")
                .addResourceLocations("file:./", "classpath:/static/")
                .setCacheControl(org.springframework.http.CacheControl.noCache().mustRevalidate());
    }

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/").setViewName("forward:/index.html");
        registry.addViewController("/home").setViewName("forward:/index.html");
        registry.addViewController("/packages").setViewName("forward:/packages.html");
        registry.addViewController("/destinations").setViewName("forward:/destinations.html");
        registry.addViewController("/transport").setViewName("forward:/transport.html");
        registry.addViewController("/hotels").setViewName("forward:/hotels.html");
        registry.addViewController("/login").setViewName("forward:/login.html");
        registry.addViewController("/register").setViewName("forward:/register.html");
        registry.addViewController("/profile").setViewName("forward:/profile.html");
        registry.addViewController("/admin").setViewName("forward:/admin/dashboard.html");
        registry.addViewController("/admin/").setViewName("forward:/admin/dashboard.html");
    }
}
