package com.PoolingAPI.Document;


import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.persistence.Column;
import javax.persistence.Id;
import java.sql.Timestamp;

@Document
public class DrugDocument {

    @Id
    private ObjectId _id;

    private Integer drug_id;

    private String din;

    private String brand_name;

    private String generic_name;

    private String strength;

    private Boolean drug_active;

    private String equivalent_to;

    private Integer brand_generic_type;

    private String default_sig;

    private String drugForm;

    private Integer form_id;

    private Integer shape_id;

    private String pharmacyID;

    private Timestamp insertTime;

    private Integer PackSize;

    private Boolean drugPackActive;

    private Integer drugPackID;

    public ObjectId get_id() {
        return _id;
    }

    public void set_id(ObjectId _id) {
        this._id = _id;
    }

    public Integer getDrug_id() {
        return drug_id;
    }

    public void setDrug_id(Integer drug_id) {
        this.drug_id = drug_id;
    }

    public String getDin() {
        return din;
    }

    public void setDin(String din) {
        this.din = din;
    }

    public String getBrand_name() {
        return brand_name;
    }

    public void setBrand_name(String brand_name) {
        this.brand_name = brand_name;
    }

    public String getGeneric_name() {
        return generic_name;
    }

    public void setGeneric_name(String generic_name) {
        this.generic_name = generic_name;
    }

    public String getStrength() {
        return strength;
    }

    public void setStrength(String strength) {
        this.strength = strength;
    }

    public Boolean getDrug_active() {
        return drug_active;
    }

    public void setDrug_active(Boolean drug_active) {
        this.drug_active = drug_active;
    }

    public String getEquivalent_to() {
        return equivalent_to;
    }

    public void setEquivalent_to(String equivalent_to) {
        this.equivalent_to = equivalent_to;
    }

    public Integer getBrand_generic_type() {
        return brand_generic_type;
    }

    public void setBrand_generic_type(Integer brand_generic_type) {
        this.brand_generic_type = brand_generic_type;
    }

    public String getDefault_sig() {
        return default_sig;
    }

    public void setDefault_sig(String default_sig) {
        this.default_sig = default_sig;
    }

    public String getDrugForm() {
        return drugForm;
    }

    public void setDrugForm(String drugForm) {
        this.drugForm = drugForm;
    }

    public Integer getForm_id() {
        return form_id;
    }

    public void setForm_id(Integer form_id) {
        this.form_id = form_id;
    }

    public Integer getShape_id() {
        return shape_id;
    }

    public void setShape_id(Integer shape_id) {
        this.shape_id = shape_id;
    }

    public String getPharmacyID() {
        return pharmacyID;
    }

    public void setPharmacyID(String pharmacyID) {
        this.pharmacyID = pharmacyID;
    }

    public Timestamp getInsertTime() {
        return insertTime;
    }

    public void setInsertTime(Timestamp insertTime) {
        this.insertTime = insertTime;
    }

    public Integer getPackSize() {
        return PackSize;
    }

    public void setPackSize(Integer packSize) {
        PackSize = packSize;
    }

    public Boolean getDrugPackActive() {
        return drugPackActive;
    }

    public void setDrugPackActive(Boolean drugPackActive) {
        this.drugPackActive = drugPackActive;
    }

    public Integer getDrugPackID() {
        return drugPackID;
    }

    public void setDrugPackID(Integer drugPackID) {
        this.drugPackID = drugPackID;
    }
}
