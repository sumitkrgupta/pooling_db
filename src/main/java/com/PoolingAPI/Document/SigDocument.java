package com.PoolingAPI.Document;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.persistence.Id;
import java.sql.Timestamp;

@Document
public class SigDocument {

    @Id
    private ObjectId _id;
    private Integer sig_id;
    private String token;
    private String sig_text;
    private boolean is_route_of_admin;
    private String Pharmacy_ID;
    private Timestamp insertTime;

    public ObjectId get_id() {
        return _id;
    }

    public void set_id(ObjectId _id) {
        this._id = _id;
    }

    public String getPharmacy_ID() {
        return Pharmacy_ID;
    }

    public void setPharmacy_ID(String pharmacy_ID) {
        Pharmacy_ID = pharmacy_ID;
    }

    public Timestamp getInsertTime() {
        return insertTime;
    }

    public void setInsertTime(Timestamp insertTime) {
        this.insertTime = insertTime;
    }

    public Integer getSig_id() {
        return sig_id;
    }

    public void setSig_id(Integer sig_id) {
        this.sig_id = sig_id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getSig_text() {
        return sig_text;
    }

    public void setSig_text(String sig_text) {
        this.sig_text = sig_text;
    }

    public boolean isIs_route_of_admin() {
        return is_route_of_admin;
    }

    public void setIs_route_of_admin(boolean is_route_of_admin) {
        this.is_route_of_admin = is_route_of_admin;
    }

    @Override
    public String toString() {
        return "SigCollection{" +
                "_id=" + _id +
                ", sig_id=" + sig_id +
                ", token='" + token + '\'' +
                ", sig_text='" + sig_text + '\'' +
                ", is_route_of_admin=" + is_route_of_admin +
                ", Pharmacy_ID='" + Pharmacy_ID + '\'' +
                ", insertTime=" + insertTime +
                '}';
    }
}
