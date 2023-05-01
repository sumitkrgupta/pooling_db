package com.PoolingAPI.Model;


import org.springframework.stereotype.Component;

import javax.persistence.Entity;
import java.io.Serializable;
import java.sql.Timestamp;

@Component
public class Doc implements Serializable {

    private Integer ID;

    private String LastName;

    private String FirstName;

    private String Salutation;

    private Integer Designation;

    private String Specialty;

    private Timestamp Created;

    private Timestamp Changed;

    private Integer DispensingRights;

    private boolean Active;

    private String Licence1;

    private String Description;

    private String Phone;

    public Doc() {
        super();
    }


    public Doc(Integer ID, String lastName, String firstName, String salutation, Integer designation, String specialty, Timestamp created, Timestamp changed, Integer dispensingRights, boolean active, String licence1, String description, String phone) {
        this.ID = ID;
        LastName = lastName;
        FirstName = firstName;
        Salutation = salutation;
        Designation = designation;
        Specialty = specialty;
        Created = created;
        Changed = changed;
        DispensingRights = dispensingRights;
        Active = active;
        Licence1 = licence1;
        Description = description;
        Phone = phone;
    }

    public Integer getID() {
        return ID;
    }

    public void setID(Integer ID) {
        this.ID = ID;
    }

    public String getLastName() {
        return LastName;
    }

    public void setLastName(String lastName) {
        LastName = lastName;
    }

    public String getFirstName() {
        return FirstName;
    }

    public void setFirstName(String firstName) {
        FirstName = firstName;
    }

    public String getSalutation() {
        return Salutation;
    }

    public void setSalutation(String salutation) {
        Salutation = salutation;
    }

    public Integer getDesignation() {
        return Designation;
    }

    public void setDesignation(Integer designation) {
        Designation = designation;
    }

    public String getSpecialty() {
        return Specialty;
    }

    public void setSpecialty(String specialty) {
        Specialty = specialty;
    }

    public Timestamp getCreated() {
        return Created;
    }

    public void setCreated(Timestamp created) {
        Created = created;
    }

    public Timestamp getChanged() {
        return Changed;
    }

    public void setChanged(Timestamp changed) {
        Changed = changed;
    }

    public Integer getDispensingRights() {
        return DispensingRights;
    }

    public void setDispensingRights(Integer dispensingRights) {
        DispensingRights = dispensingRights;
    }

    public boolean isActive() {
        return Active;
    }

    public void setActive(boolean active) {
        Active = active;
    }

    public String getLicence1() {
        return Licence1;
    }

    public void setLicence1(String licence1) {
        Licence1 = licence1;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public String getPhone() {
        return Phone;
    }

    public void setPhone(String phone) {
        Phone = phone;
    }

    @Override
    public String toString() {
        return "Doc{" +
                "ID=" + ID +
                ", LastName='" + LastName + '\'' +
                ", FirstName='" + FirstName + '\'' +
                ", Salutation='" + Salutation + '\'' +
                ", Designation=" + Designation +
                ", Specialty='" + Specialty + '\'' +
                ", Created=" + Created +
                ", Changed=" + Changed +
                ", DispensingRights=" + DispensingRights +
                ", Active=" + Active +
                ", Licence1='" + Licence1 + '\'' +
                ", Description='" + Description + '\'' +
                ", Phone='" + Phone + '\'' +
                '}';
    }
}
