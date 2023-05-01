package com.PoolingAPI.Model;

import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.sql.Timestamp;
import java.time.LocalDate;

@Component
public class Pat implements Serializable {

    private Integer ID;
    private String LastName;
    private String FirstName;
    private LocalDate Birthday;
    private String Sex;
    private Timestamp CreatedOn;
    private Timestamp LastChanged;
    private String AlternateLastName;
    private String Description;
    private String Phone;

    public Pat() {
        super();
    }

    public Pat(Integer ID, String lastName, String firstName, LocalDate birthday, String sex, Timestamp createdOn, Timestamp lastChanged, String alternateLastName, String description, String phone) {
        this.ID = ID;
        LastName = lastName;
        FirstName = firstName;
        Birthday = birthday;
        Sex = sex;
        CreatedOn = createdOn;
        LastChanged = lastChanged;
        AlternateLastName = alternateLastName;
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

    public LocalDate getBirthday() {
        return Birthday;
    }

    public void setBirthday(LocalDate birthday) {
        Birthday = birthday;
    }

    public String getSex() {
        return Sex;
    }

    public void setSex(String sex) {
        Sex = sex;
    }

    public Timestamp getCreatedOn() {
        return CreatedOn;
    }

    public void setCreatedOn(Timestamp createdOn) {
        CreatedOn = createdOn;
    }

    public Timestamp getLastChanged() {
        return LastChanged;
    }

    public void setLastChanged(Timestamp lastChanged) {
        LastChanged = lastChanged;
    }

    public String getAlternateLastName() {
        return AlternateLastName;
    }

    public void setAlternateLastName(String alternateLastName) {
        AlternateLastName = alternateLastName;
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
        return "Pat{" +
                "ID=" + ID +
                ", LastName='" + LastName + '\'' +
                ", FirstName='" + FirstName + '\'' +
                ", Birthday=" + Birthday +
                ", Sex='" + Sex + '\'' +
                ", CreatedOn=" + CreatedOn +
                ", LastChanged=" + LastChanged +
                ", AlternateLastName='" + AlternateLastName + '\'' +
                ", Description='" + Description + '\'' +
                ", Phone='" + Phone + '\'' +
                '}';
    }
}