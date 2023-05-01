package com.PoolingAPI.Document;

import org.springframework.data.mongodb.core.mapping.Document;

import java.sql.Timestamp;
import java.time.LocalDate;


@Document
public class PatientsDocument {

    private Integer pat_id;
    private String lastname;
    private String firstname;
    private LocalDate birthday;
    private String sex;
    private Timestamp createdon;
    private Timestamp lastchanged;
    private String alternatelastname;
    private String Description;
    private String phone;
    private Timestamp insertTime;
    private String matchKey;
    private String PharmacyId;

    public Integer getPat_id() {
        return pat_id;
    }

    public void setPat_id(Integer pat_id) {
        this.pat_id = pat_id;
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

    public LocalDate getBirthday() {
        return birthday;
    }

    public void setBirthday(LocalDate birthday) {
        this.birthday = birthday;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public Timestamp getCreatedon() {
        return createdon;
    }

    public void setCreatedon(Timestamp createdon) {
        this.createdon = createdon;
    }

    public Timestamp getLastchanged() {
        return lastchanged;
    }

    public void setLastchanged(Timestamp lastchanged) {
        this.lastchanged = lastchanged;
    }

    public String getAlternatelastname() {
        return alternatelastname;
    }

    public void setAlternatelastname(String alternatelastname) {
        this.alternatelastname = alternatelastname;
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
        return "PatientsDocument{" +
                "pat_id=" + pat_id +
                ", lastname='" + lastname + '\'' +
                ", firstname='" + firstname + '\'' +
                ", birthday=" + birthday +
                ", sex='" + sex + '\'' +
                ", createdon=" + createdon +
                ", lastchanged=" + lastchanged +
                ", alternatelastname='" + alternatelastname + '\'' +
                ", Description='" + Description + '\'' +
                ", phone='" + phone + '\'' +
                ", insertTime=" + insertTime +
                ", matchKey='" + matchKey + '\'' +
                ", PharmacyId='" + PharmacyId + '\'' +
                '}';
    }
}
