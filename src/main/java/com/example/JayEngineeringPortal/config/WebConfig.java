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

        // ✅ Allowed Frontend Origins (add all your Netlify preview URLs if needed)
        config.setAllowedOrigins(Arrays.asList(
                "https://jayengineering.netlify.app",
                "https://69145147c910d53131927ad1--jayengineering.netlify.app",
                "https://6914554f61dc38056006fe7b--jayengineering.netlify.app",
                "http://localhost:4200"
        ));

        // ✅ Allow credentials and all common headers/methods
        config.setAllowCredentials(true);
        config.addAllowedHeader("*");
        config.addAllowedMethod("*");

        // ✅ This fixes OPTIONS preflight issue
        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return new CorsFilter(source);
    }
}
