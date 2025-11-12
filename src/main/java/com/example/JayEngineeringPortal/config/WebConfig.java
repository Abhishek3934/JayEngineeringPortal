package com.example.JayEngineeringPortal.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.Arrays;

@Configuration
public class WebConfig {

    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();

        // ✅ Allow both your Netlify URLs (main + preview)
        config.setAllowedOrigins(Arrays.asList(
                "https://jayengineering.netlify.app",
                "https://6913975361dc385c9a06fcc5--jayengineering.netlify.app"
        ));

        // ✅ Allow all necessary HTTP methods
        config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));

        // ✅ Allow all headers
        config.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "Accept"));

        // ✅ Expose Authorization header if you use JWT or tokens
        config.setExposedHeaders(Arrays.asList("Authorization"));

        // ✅ Allow sending cookies / credentials
        config.setAllowCredentials(true);

        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return new CorsFilter(source);
    }
}
