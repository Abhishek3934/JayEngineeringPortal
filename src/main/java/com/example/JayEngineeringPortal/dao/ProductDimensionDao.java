package com.example.JayEngineeringPortal.dao;

import com.example.JayEngineeringPortal.model.ProductDimension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class ProductDimensionDao {

    @Autowired
    private JdbcTemplate jdbc;

    public int addDimension(ProductDimension d) {

        System.out.println("------------- New Dimension Request -------------");

        if(d == null) {
            System.out.println("❌ ERROR: ProductDimension object is NULL");
            return 0;
        }

        System.out.println("✅ Received Data from Frontend:");
        System.out.println("Product ID        : " + d.getProductId());
        System.out.println("SR No            : " + d.getSrNo());
        System.out.println("Dimension Type   : " + d.getDimensionType());
        System.out.println("Specified Dim    : " + d.getSpecifiedDimension());
        System.out.println("Tolerance        : " + d.getTolerance());

        // Null Safety check
        if (d.getProductId() == null) {
            System.out.println("❌ ERROR: productId is NULL");
            return 0;
        }

        String sql = "INSERT INTO product_dimensions (product_id, sr_no, dimension_type, specified_dimension, tolerance) VALUES (?, ?, ?, ?, ?)";

        try {
            int rows = jdbc.update(sql,
                    d.getProductId(),
                    d.getSrNo(),
                    d.getDimensionType(),
                    d.getSpecifiedDimension(),
                    d.getTolerance()
            );

            System.out.println("✅ DB Insert Successful, Rows = " + rows);
            System.out.println("------------------------------------------------");
            return rows;

        } catch (Exception ex) {
            System.out.println("❌ ERROR while inserting dimension: " + ex.getMessage());
            ex.printStackTrace();
            return 0;
        }
    }

    public List<ProductDimension> findByProductId(Long productId) {
        String sql = "SELECT * FROM product_dimensions WHERE product_id = ? ORDER BY sr_no";
        return jdbc.query(sql, new Object[]{productId}, (rs, rn) -> mapDimension(rs));
    }

    private ProductDimension mapDimension(ResultSet rs) throws SQLException {
        ProductDimension d = new ProductDimension();
        d.setId(rs.getLong("id"));
        d.setProductId(rs.getLong("product_id"));
        d.setSrNo(rs.getInt("sr_no"));
        d.setDimensionType(rs.getString("dimension_type"));
        d.setSpecifiedDimension(rs.getString("specified_dimension"));
        d.setTolerance(rs.getString("tolerance"));
        return d;
    }
}
