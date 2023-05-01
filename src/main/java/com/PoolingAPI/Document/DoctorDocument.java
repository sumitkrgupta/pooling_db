package com.PoolingAPI.Document;

import org.springframework.data.mongodb.core.mapping.Document;
import java.sql.Timestamp;


@Document
public class DoctorDocument {

    private Integer doc_id;
    private String lastname;
    private String firstname;
    private String salutation;
    private Integer designation;
    private String specialty;
    private Timestamp created;
    private Timestamp changed;
    private Integer dispensingrights;
    private Boolean active;
    private String Description;
    private String phone;
    private Timestamp insertTime;
    private String matchKey;
    private String PharmacyId;
    private String Licence_Number;

    public String getLicence_Number() {
        return Licence_Number;
    }

    public void setLicence_Number(String licence_Number) {
        Licence_Number = licence_Number;
    }

    public Integer getDoc_id() {
        return doc_id;
    }

    public void setDoc_id(Integer doc_id) {
        this.doc_id = doc_id;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getSalutation() {
        return salutation;
    }

    public void setSalutation(String salutation) {
        this.salutation = salutation;
    }

    public Integer getDesignation() {
        return designation;
    }

    public void setDesignation(Integer designation) {
        this.designation = designation;
    }

    public String getSpecialty() {
        return specialty;
    }

    public void setSpecialty(String specialty) {
        this.specialty = specialty;
    }

    public Timestamp getCreated() {
        return created;
    }

    public void setCreated(Timestamp created) {
        this.created = created;
    }

    public Timestamp getChanged() {
        return changed;
    }

    public void setChanged(Timestamp changed) {
        this.changed = changed;
    }

    public Integer getDispensingrights() {
        return dispensingrights;
    }

    public void setDispensingrights(Integer dispensingrights) {
        this.dispensingrights = dispensingrights;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Timestamp getInsertTime() {
        return insertTime;
    }

    public void setInsertTime(Timestamp insertTime) {
        this.insertTime = insertTime;
    }

    public String getMatchKey() {
        return matchKey;
    }

    public void setMatchKey(String matchKey) {
        this.matchKey = matchKey;
    }

    public String getPharmacyId() {
        return PharmacyId;
    }

    public void setPharmacyId(String pharmacyId) {
        PharmacyId = pharmacyId;
    }

    @Override
    public String toString() {
        return "DoctorDocument{" +
                "doc_id=" + doc_id +
                ", lastname='" + lastname + '\'' +
                ", firstname='" + firstname + '\'' +
                ", salutation='" + salutation + '\'' +
                ", designation=" + designation +
                ", specialty='" + specialty + '\'' +
                ", created=" + created +
                ", changed=" + changed +
                ", dispensingrights=" + dispensingrights +
                ", active=" + active +
                ", Description='" + Description + '\'' +
                ", phone='" + phone + '\'' +
                ", insertTime=" + insertTime +
                ", matchKey='" + matchKey + '\'' +
                ", PharmacyId='" + PharmacyId + '\'' +
                ", Licence_Number='" + Licence_Number + '\'' +
                '}';
    }
}
