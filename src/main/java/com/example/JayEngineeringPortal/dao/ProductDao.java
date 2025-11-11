package com.example.JayEngineeringPortal.dao;

import com.example.JayEngineeringPortal.model.Product;
import com.example.JayEngineeringPortal.model.ProductDimension;
import com.example.JayEngineeringPortal.model.SurfaceTreatment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

@Repository
public class ProductDao {

    @Autowired
    private JdbcTemplate jdbc;

    // ✅ Add Product and return generated ID
    public Long addProduct(Product p) {
        String sql = "INSERT INTO products (project_no, part_name, drawing_no, customer, material, order_qty, other_details) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbc.update(con -> {
            PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, p.getProjectNo());
            ps.setString(2, p.getPartName());
            ps.setString(3, p.getDrawingNo());
            ps.setString(4, p.getCustomer());
            ps.setString(5, p.getMaterial());
            if (p.getOrderQty() != null)
                ps.setInt(6, p.getOrderQty());
            else
                ps.setNull(6, java.sql.Types.INTEGER);
            ps.setString(7, p.getOtherDetails());
            return ps;
        }, keyHolder);

        Number key = keyHolder.getKey();
        return key != null ? key.longValue() : null;
    }


    // ✅ Find Product by ID
    public Product findProductById(Long id) {
        String sql = "SELECT * FROM products WHERE id = ?";
        List<Product> list = jdbc.query(sql, new Object[]{id}, (rs, rowNum) -> {
            Product p = mapProduct(rs);
            p.setDimensions(findDimensionsByProductId(p.getId()));
            p.setSurfaceTreatment(findSurfaceByProductId(p.getId()));
            return p;
        });
        return list.isEmpty() ? null : list.get(0);
    }

    // ✅ Find Product by Drawing No (existing)
    public Product findProductByDrawing(String keyword) {
        String sql = "SELECT * FROM products WHERE LOWER(drawing_no) = LOWER(?) OR LOWER(part_name) = LOWER(?)";
        List<Product> list = jdbc.query(sql, new Object[]{keyword, keyword}, (rs, rowNum) -> {
            Product p = mapProduct(rs);
            p.setDimensions(findDimensionsByProductId(p.getId()));
            p.setSurfaceTreatment(findSurfaceByProductId(p.getId()));
            return p;
        });
        return list.isEmpty() ? null : list.get(0);
    }

    // ✅ Text search
    public List<Product> searchProducts(String keyword) {
        String sql = "SELECT * FROM products " +
                     "WHERE LOWER(part_name) LIKE ? " +
                     "OR LOWER(project_no) LIKE ? " +
                     "OR LOWER(drawing_no) LIKE ? " +
                     "OR LOWER(customer) LIKE ?";
        String like = "%" + keyword.toLowerCase() + "%";
        return jdbc.query(sql, new Object[]{like, like, like, like}, (rs, rowNum) -> {
            Product p = mapProduct(rs);
            p.setDimensions(findDimensionsByProductId(p.getId()));
            p.setSurfaceTreatment(findSurfaceByProductId(p.getId()));
            return p;
        });
    }

    // ✅ Map Product
    private Product mapProduct(ResultSet rs) throws SQLException {
        Product p = new Product();
        p.setId(rs.getLong("id"));
        p.setProjectNo(rs.getString("project_no"));
        p.setPartName(rs.getString("part_name"));
        p.setDrawingNo(rs.getString("drawing_no"));
        p.setCustomer(rs.getString("customer"));
        p.setMaterial(rs.getString("material"));
        p.setOrderQty(rs.getInt("order_qty"));
        p.setOtherDetails(rs.getString("other_details"));
        return p;
    }

    // ✅ Get Dimensions
    private List<ProductDimension> findDimensionsByProductId(Long productId) {
        String sql = "SELECT * FROM product_dimensions WHERE product_id = ? ORDER BY sr_no";
        return jdbc.query(sql, new Object[]{productId}, (rs, rowNum) -> mapDimension(rs));
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

    // ✅ Surface Treatment
    private SurfaceTreatment findSurfaceByProductId(Long productId) {
        String sql = "SELECT * FROM surface_treatment WHERE product_id = ?";
        List<SurfaceTreatment> list = jdbc.query(sql, new Object[]{productId}, (rs, rowNum) -> mapSurface(rs));
        return list.isEmpty() ? null : list.get(0);
    }

    private SurfaceTreatment mapSurface(ResultSet rs) throws SQLException {
        SurfaceTreatment s = new SurfaceTreatment();
        s.setId(rs.getLong("id"));
        s.setProductId(rs.getLong("product_id"));
        s.setBuffing(rs.getBoolean("buffing"));
        s.setMirrorBuffing(rs.getBoolean("mirror_buffing"));
        s.setBlackodising(rs.getBoolean("blackodising"));
        s.setAnodising(rs.getBoolean("anodising"));
        s.setHardchrome(rs.getBoolean("hardchrome"));
        s.setOther(rs.getString("other"));
        s.setRemarks(rs.getString("remarks"));
        s.setInspectedBy(rs.getString("inspected_by"));
        s.setApprovedBy(rs.getString("approved_by"));
        return s;
    }
}
