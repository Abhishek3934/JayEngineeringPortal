package com.example.JayEngineeringPortal.model;

public class SurfaceTreatment {
    private Long id;
    private Long productId;
    private Boolean buffing;
    private Boolean mirrorBuffing;
    private Boolean blackodising;
    private Boolean anodising;
    private Boolean hardchrome;
    private String other;
    private String remarks;
    private String inspectedBy;
    private String approvedBy;

    // getters / setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }

    public Boolean getBuffing() { return buffing; }
    public void setBuffing(Boolean buffing) { this.buffing = buffing; }

    public Boolean getMirrorBuffing() { return mirrorBuffing; }
    public void setMirrorBuffing(Boolean mirrorBuffing) { this.mirrorBuffing = mirrorBuffing; }

    public Boolean getBlackodising() { return blackodising; }
    public void setBlackodising(Boolean blackodising) { this.blackodising = blackodising; }

    public Boolean getAnodising() { return anodising; }
    public void setAnodising(Boolean anodising) { this.anodising = anodising; }

    public Boolean getHardchrome() { return hardchrome; }
    public void setHardchrome(Boolean hardchrome) { this.hardchrome = hardchrome; }

    public String getOther() { return other; }
    public void setOther(String other) { this.other = other; }

    public String getRemarks() { return remarks; }
    public void setRemarks(String remarks) { this.remarks = remarks; }

    public String getInspectedBy() { return inspectedBy; }
    public void setInspectedBy(String inspectedBy) { this.inspectedBy = inspectedBy; }

    public String getApprovedBy() { return approvedBy; }
    public void setApprovedBy(String approvedBy) { this.approvedBy = approvedBy; }
}
