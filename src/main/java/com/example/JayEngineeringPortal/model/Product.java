package com.example.JayEngineeringPortal.model;

import java.time.LocalDate;
import java.util.List;

public class Product {
    private Long id;
    private String projectNo;
    private String partName;
    private String drawingNo;
    private String customer;
    private String material;
    private Integer orderQty;
    private String otherDetails;

    // optional lists
    private List<ProductDimension> dimensions;
    private SurfaceTreatment surfaceTreatment;
	private String inspectionDate;

    // getters / setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getProjectNo() { return projectNo; }
    public void setProjectNo(String projectNo) { this.projectNo = projectNo; }

    public String getPartName() { return partName; }
    public void setPartName(String partName) { this.partName = partName; }

    public String getDrawingNo() { return drawingNo; }
    public void setDrawingNo(String drawingNo) { this.drawingNo = drawingNo; }

    public String getCustomer() { return customer; }
    public void setCustomer(String customer) { this.customer = customer; }

    public String getMaterial() { return material; }
    public void setMaterial(String material) { this.material = material; }

    public Integer getOrderQty() { return orderQty; }
    public void setOrderQty(Integer orderQty) { this.orderQty = orderQty; }

    public String getOtherDetails() { return otherDetails; }
    public void setOtherDetails(String otherDetails) { this.otherDetails = otherDetails; }

    public List<ProductDimension> getDimensions() { return dimensions; }
    public void setDimensions(List<ProductDimension> dimensions) { this.dimensions = dimensions; }

    public SurfaceTreatment getSurfaceTreatment() { return surfaceTreatment; }
    public void setSurfaceTreatment(SurfaceTreatment surfaceTreatment) { this.surfaceTreatment = surfaceTreatment; }
	public String getInspectionDate() {
    return inspectionDate;
}

public void setInspectionDate(String string) {
    this.inspectionDate = string;
}
public void setInspectionReportNo(String string) {
	// TODO Auto-generated method stub
	
}

	
}
