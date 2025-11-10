package com.example.JayEngineeringPortal.model;

public class Dimension {
    private int product_id;
    private int sr_no;
    private String dimension_type;
    private String specified_dimension;
    private String tolerance;

    public int getProduct_id() { return product_id; }
    public void setProduct_id(int product_id) { this.product_id = product_id; }

    public int getSr_no() { return sr_no; }
    public void setSr_no(int sr_no) { this.sr_no = sr_no; }

    public String getDimension_type() { return dimension_type; }
    public void setDimension_type(String dimension_type) { this.dimension_type = dimension_type; }

    public String getSpecified_dimension() { return specified_dimension; }
    public void setSpecified_dimension(String specified_dimension) { this.specified_dimension = specified_dimension; }

    public String getTolerance() { return tolerance; }
    public void setTolerance(String tolerance) { this.tolerance = tolerance; }

    // New methods to match ReportService
    public String getDimensionType() {
        return this.dimension_type;
    }

    public String getSpecifiedDimension() {
        return this.specified_dimension;
    }
}
