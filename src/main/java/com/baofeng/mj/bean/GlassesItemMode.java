package com.baofeng.mj.bean;

/**
 * Created by muyu on 2016/5/19.
 */
public class GlassesItemMode {
    private String ManufacturerName;
    private String ProductName;
    private String GlassesName;
    private String productKey;
    private String glassesKey;
    private String GlassesID;
    private String ProductID;
    private String ManufactureID;
    private String DisplayName;   //用于页面显示的name
    private boolean isSelected = false;
    public GlassesItemMode(String manufacturerName, String productName,
                           String glassesName, String productKey, String glassesKey,String ManufactureID,String ProductID,String GlassesID,String displayName) {
        super();
        ManufacturerName = manufacturerName;
        ProductName = productName;
        GlassesName = glassesName;
        this.productKey = productKey;
        this.glassesKey = glassesKey;
        this.ManufactureID = ManufactureID;
        this.ProductID = ProductID;
        this.GlassesID = GlassesID;
        this.DisplayName = displayName;
    }

    public GlassesItemMode() {
        super();
    }

    public String getManufacturerName() {
        return ManufacturerName;
    }
    public void setManufacturerName(String manufacturerName) {
        ManufacturerName = manufacturerName;
    }
    public String getProductName() {
        return ProductName;
    }
    public void setProductName(String productName) {
        ProductName = productName;
    }
    public String getGlassesName() {
        return GlassesName;
    }
    public void setGlassesName(String glassesName) {
        GlassesName = glassesName;
    }
    public String getProductKey() {
        return productKey;
    }
    public void setProductKey(String productKey) {
        this.productKey = productKey;
    }
    public String getGlassesKey() {
        return glassesKey;
    }
    public void setGlassesKey(String glassesKey) {
        this.glassesKey = glassesKey;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean isSelected) {
        this.isSelected = isSelected;
    }

    public String getGlassesID() {
        return GlassesID;
    }

    public void setGlassesID(String glassesID) {
        GlassesID = glassesID;
    }

    public String getProductID() {
        return ProductID;
    }

    public void setProductID(String productID) {
        ProductID = productID;
    }

    public String getManufactureID() {
        return ManufactureID;
    }

    public void setManufactureID(String manufactureID) {
        ManufactureID = manufactureID;
    }

    public String getDisplayName() {
        return DisplayName;
    }

    public void setDisplayName(String displayName) {
        DisplayName = displayName;
    }


}

