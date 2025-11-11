package com.example.JayEngineeringPortal.controller;

import com.example.JayEngineeringPortal.model.ProductDimension;
import com.example.JayEngineeringPortal.service.AppService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@CrossOrigin(
	    origins = {
	        "https://jayengineering.netlify.app",
	        "https://69138be527cb924248693061--jayengineering.netlify.app",
	        "http://localhost:4200"
	    }
	)

@RestController
@RequestMapping("/api/dimensions")
public class DimensionController {

    @Autowired
    private AppService appService;

    @PostMapping("/add")
    public ResponseEntity<?> addProductDimension(@RequestBody ProductDimension dim) {
        System.out.println("✅ Received Dimension Data: " + dim);

        try {
            boolean status = appService.addDimension(dim);

            if (status) {
                return ResponseEntity.ok("Dimension added successfully");
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("Failed to add dimension");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Exception: " + e.getMessage());
        }
    }
}
