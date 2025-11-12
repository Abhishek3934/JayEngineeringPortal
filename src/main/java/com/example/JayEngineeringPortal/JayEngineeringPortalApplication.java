package com.example.JayEngineeringPortal;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class JayEngineeringPortalApplication {

    public static void main(String[] args) {

        // If PORT is set (Railway), activate "cloud" profile
        if (System.getenv("PORT") != null) {
            System.setProperty("spring.profiles.active", "cloud");
        }

        SpringApplication.run(JayEngineeringPortalApplication.class, args);
        System.out.println("Hello Abhishek");
    }
}
