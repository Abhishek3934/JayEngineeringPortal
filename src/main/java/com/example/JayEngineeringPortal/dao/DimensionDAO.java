package com.example.JayEngineeringPortal.dao;

import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.example.JayEngineeringPortal.model.Dimension;

@Repository
public class DimensionDAO {

    private final JdbcTemplate jdbcTemplate;

    public DimensionDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public int save(Dimension dim) {
        String sql = "INSERT INTO product_dimensions (product_id, sr_no, dimension_type, specified_dimension, tolerance) VALUES (?,?,?,?,?)";
        return jdbcTemplate.update(sql,
                dim.getProduct_id(),
                dim.getSr_no(),
                dim.getDimension_type(),
                dim.getSpecified_dimension(),
                dim.getTolerance()
        );
    }

    // New method required by ReportService
    public List<Dimension> getDimensionsByProductId(Long productId) {
        String sql = "SELECT * FROM product_dimensions WHERE product_id = ?";
        return jdbcTemplate.query(sql, new Object[]{productId}, (rs, rowNum) -> {
            Dimension d = new Dimension();
            d.setProduct_id(rs.getInt("product_id"));
            d.setSr_no(rs.getInt("sr_no"));
            d.setDimension_type(rs.getString("dimension_type"));
            d.setSpecified_dimension(rs.getString("specified_dimension"));
            d.setTolerance(rs.getString("tolerance"));
            return d;
        });
    }
}
