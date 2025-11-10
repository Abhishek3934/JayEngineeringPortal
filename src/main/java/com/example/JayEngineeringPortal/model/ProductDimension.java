package com.example.JayEngineeringPortal.model;

public class ProductDimension {

    private Long id;
    private Long productId;
    private Integer srNo;
    private String dimensionType;
    private String specifiedDimension;
    private String tolerance;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }

    public Integer getSrNo() { return srNo; }
    public void setSrNo(Integer srNo) { this.srNo = srNo; }

    public String getDimensionType() { return dimensionType; }
    public void setDimensionType(String dimensionType) { this.dimensionType = dimensionType; }

    public String getSpecifiedDimension() { return specifiedDimension; }
    public void setSpecifiedDimension(String specifiedDimension) { this.specifiedDimension = specifiedDimension; }

    public String getTolerance() { return tolerance; }
    public void setTolerance(String tolerance) { this.tolerance = tolerance; }
}
