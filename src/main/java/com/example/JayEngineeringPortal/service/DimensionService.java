package com.example.JayEngineeringPortal.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import com.example.JayEngineeringPortal.model.ProductDimension;

@Service
public class DimensionService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public boolean saveDimension(ProductDimension dim) {

        String sql = "INSERT INTO product_dimensions (product_id, sr_no, dimension_type, specified_dimension, tolerance) "
                   + "VALUES (?, ?, ?, ?, ?)";

        int rows = jdbcTemplate.update(sql,
                dim.getProductId(),
                dim.getSrNo(),
                dim.getDimensionType(),
                dim.getSpecifiedDimension(),
                dim.getTolerance()
        );

        return rows > 0; // ✅ returns true only if inserted
    }
}
