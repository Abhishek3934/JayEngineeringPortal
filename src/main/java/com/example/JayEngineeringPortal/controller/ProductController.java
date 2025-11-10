package com.example.JayEngineeringPortal.controller;

import com.example.JayEngineeringPortal.model.Product;
import com.example.JayEngineeringPortal.service.AppService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/products")
@CrossOrigin(origins = "*")
public class ProductController {

    @Autowired
    private AppService appService;

    // ✅ Add Product
    @PostMapping("/add-product")
    public ResponseEntity<Map<String, Object>> addProduct(@RequestBody Product product) {
        try {
            // Save product and get saved entity with ID
            Product savedProduct = appService.saveProduct(product);

            Map<String, Object> response = new HashMap<>();
            response.put("productId", savedProduct.getId()); // return ID for adding dimensions
            response.put("message", "Product added successfully");
            response.put("product", savedProduct); // optional: full product details

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            Map<String, Object> error = new HashMap<>();
            error.put("message", "Failed to add product");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    // ✅ Search by Drawing No
    @GetMapping("/search")
    public ResponseEntity<?> search(@RequestParam("drawingNo") String drawingNo) {
        Product p = appService.findProductByDrawing(drawingNo);
        if (p == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("NOT_FOUND");
        }
        return ResponseEntity.ok(p);
    }

    // ✅ Optional text search
    @GetMapping("/search/text")
    public ResponseEntity<?> searchText(@RequestParam("keyword") String keyword) {
        List<Product> products = appService.searchProducts(keyword);
        if (products.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("NOT_FOUND");
        }
        return ResponseEntity.ok(products);
    }

    // ✅ File Serving
    @GetMapping("/file")
    public ResponseEntity<?> serveFile(@RequestParam("path") String path) {
        File f = new File(path);
        if (!f.exists()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("FILE_NOT_FOUND");
        }
        FileSystemResource resource = new FileSystemResource(f);
        MediaType mediaType = path.toLowerCase().endsWith(".pdf")
                ? MediaType.APPLICATION_PDF
                : MediaType.APPLICATION_OCTET_STREAM;

        return ResponseEntity.ok()
                .contentType(mediaType)
                .contentLength(f.length())
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + f.getName() + "\"")
                .body(resource);
    }
}
