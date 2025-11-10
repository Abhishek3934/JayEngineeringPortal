package com.example.JayEngineeringPortal.service;

import com.example.JayEngineeringPortal.dao.ProductDao;
import com.example.JayEngineeringPortal.dao.ProductDimensionDao;
import com.example.JayEngineeringPortal.dao.UserDao;
import com.example.JayEngineeringPortal.model.Product;
import com.example.JayEngineeringPortal.model.ProductDimension;
import com.example.JayEngineeringPortal.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AppService {

    @Autowired
    private UserDao userDao;

    @Autowired
    private ProductDao productDao;

    @Autowired
    private ProductDimensionDao productDimensionDao;

    // ✅ Authentication
    public Optional<User> authenticate(String username, String password) {
        Optional<User> u = userDao.findByUsername(username);
        if (u.isPresent() && u.get().getPassword().equals(password)) {
            return u;
        }
        return Optional.empty();
    }

    // ✅ Product Search
    public List<Product> searchProducts(String keyword) {
        return productDao.searchProducts(keyword);
    }

    // ✅ Find Product by Drawing No
    public Product findProductByDrawing(String keyword) {
        return productDao.findProductByDrawing(keyword);
    }

    // ✅ Add Product (returns true/false)
    public boolean addProduct(Product p) {
        try {
            Long id = productDao.addProduct(p);
            return id != null && id > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // ✅ Add Product and Return Saved Product (for controller)
    public Product saveProduct(Product p) {
        Long generatedId = productDao.addProduct(p); // insert product and get ID
        return productDao.findProductById(generatedId); // fetch saved product using ID
    }

    // ✅ Add Dimension
    public boolean addDimension(ProductDimension d) {
        try {
            int rows = productDimensionDao.addDimension(d);
            return rows > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // ✅ Get Dimensions for Product
    public List<ProductDimension> getDimensionsForProduct(Long productId) {
        return productDimensionDao.findByProductId(productId);
    }
}
