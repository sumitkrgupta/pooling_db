package com.PoolingAPI.Document;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.persistence.Id;
import java.sql.Timestamp;

@Document
public class DrugMixDocument {

    @Id
    private ObjectId _id;
    private Integer mix_id;
    private String mix_description;
    private Integer drug_mix_form_id;
    private String pharmacyID;
    private Timestamp insertTime;

    public ObjectId get_id() {
        return _id;
    }

    public void set_id(ObjectId _id) {
        this._id = _id;
    }

    public Timestamp getInsertTime() {
        return insertTime;
    }

    public void setInsertTime(Timestamp insertTime) {
        this.insertTime = insertTime;
    }

    public String getPharmacyID() {
        return pharmacyID;
    }

    public void setPharmacyID(String pharmacyID) {
        this.pharmacyID = pharmacyID;
    }

    public Integer getMix_id() {
        return mix_id;
    }

    public void setMix_id(Integer mix_id) {
        this.mix_id = mix_id;
    }

    public String getMix_description() {
        return mix_description;
    }

    public void setMix_description(String mix_description) {
        this.mix_description = mix_description;
    }

    public Integer getDrug_mix_form_id() {
        return drug_mix_form_id;
    }

    public void setDrug_mix_form_id(Integer drug_mix_form_id) {
        this.drug_mix_form_id = drug_mix_form_id;
    }

    @Override
    public String toString() {
        return "DrugMixCollections{" +
                "_id=" + _id +
                ", mix_id=" + mix_id +
                ", mix_description='" + mix_description + '\'' +
                ", drug_mix_form_id=" + drug_mix_form_id +
                ", pharmacyID='" + pharmacyID + '\'' +
                ", insertTime=" + insertTime +
                '}';
    }
}
