package com.PoolingAPI.Model;

import org.springframework.stereotype.Component;

import java.io.Serializable;


@Component
public class Drg implements Serializable {

    private Integer drgId;

    private String DIN;

    private String BrandName;

    private String GenericName;

    private String Strength;

    private Boolean drgActive;

    private String EquivTo;

    private Integer BrandGenericType;

    private String DefaultSig;

    private Integer DrgFormId;

    private String drugForm;

    private Integer ShapeId;

    private Integer PackSize;

    private Boolean drgPackActive;

    private Integer drgPackID;

    public Drg() {
        super();
    }

    public Drg(Integer drgId, String DIN, String brandName, String genericName, String strength, Boolean drgActive, String equivTo, Integer brandGenericType, String form, String defaultSig, Integer drgFormId, String drugForm, Integer shapeId, Integer packSize, Boolean drgPackActive, Integer drgPackID) {
        this.drgId = drgId;
        this.DIN = DIN;
        BrandName = brandName;
        GenericName = genericName;
        Strength = strength;
        this.drgActive = drgActive;
        EquivTo = equivTo;
        BrandGenericType = brandGenericType;
        DefaultSig = defaultSig;
        DrgFormId = drgFormId;
        this.drugForm = drugForm;
        ShapeId = shapeId;
        PackSize = packSize;
        this.drgPackActive = drgPackActive;
        this.drgPackID = drgPackID;
    }

    public String getDrugForm() {
        return drugForm;
    }

    public void setDrugForm(String drugForm) {
        this.drugForm = drugForm;
    }

    public Integer getDrgId() {
        return drgId;
    }

    public void setDrgId(Integer drgId) {
        this.drgId = drgId;
    }

    public String getDIN() {
        return DIN;
    }

    public void setDIN(String DIN) {
        this.DIN = DIN;
    }

    public String getBrandName() {
        return BrandName;
    }

    public void setBrandName(String brandName) {
        BrandName = brandName;
    }

    public String getGenericName() {
        return GenericName;
    }

    public void setGenericName(String genericName) {
        GenericName = genericName;
    }

    public String getStrength() {
        return Strength;
    }

    public void setStrength(String strength) {
        Strength = strength;
    }

    public Boolean getDrgActive() {
        return drgActive;
    }

    public void setDrgActive(Boolean drgActive) {
        this.drgActive = drgActive;
    }

    public String getEquivTo() {
        return EquivTo;
    }

    public void setEquivTo(String equivTo) {
        EquivTo = equivTo;
    }

    public Integer getBrandGenericType() {
        return BrandGenericType;
    }

    public void setBrandGenericType(Integer brandGenericType) {
        BrandGenericType = brandGenericType;
    }

    public String getDefaultSig() {
        return DefaultSig;
    }

    public void setDefaultSig(String defaultSig) {
        DefaultSig = defaultSig;
    }

    public Integer getDrgFormId() {
        return DrgFormId;
    }

    public void setDrgFormId(Integer drgFormId) {
        DrgFormId = drgFormId;
    }

    public Integer getShapeId() {
        return ShapeId;
    }

    public void setShapeId(Integer shapeId) {
        ShapeId = shapeId;
    }

    public Integer getPackSize() {
        return PackSize;
    }

    public void setPackSize(Integer packSize) {
        PackSize = packSize;
    }

    public Boolean getDrgPackActive() {
        return drgPackActive;
    }

    public void setDrgPackActive(Boolean drgPackActive) {
        this.drgPackActive = drgPackActive;
    }

    public Integer getDrgPackID() {
        return drgPackID;
    }

    public void setDrgPackID(Integer drgPackID) {
        this.drgPackID = drgPackID;
    }
}
