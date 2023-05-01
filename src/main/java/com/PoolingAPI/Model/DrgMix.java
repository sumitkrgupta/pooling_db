package com.PoolingAPI.Model;

import org.springframework.stereotype.Component;

import java.io.Serializable;

@Component
public class DrgMix implements Serializable {


    private Integer ID;
    private String Description;
    private Integer DrgFormId;

    public DrgMix() {
        super();
    }

    public DrgMix(Integer ID, String description, Integer drgFormId) {
        this.ID = ID;
        Description = description;
        DrgFormId = drgFormId;
    }

    public Integer getID() {
        return ID;
    }

    public void setID(Integer ID) {
        this.ID = ID;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public Integer getDrgFormId() {
        return DrgFormId;
    }

    public void setDrgFormId(Integer drgFormId) {
        DrgFormId = drgFormId;
    }

    @Override
    public String toString() {
        return "DrugMix{" +
                "ID=" + ID +
                ", Description='" + Description + '\'' +
                ", DrgFormId=" + DrgFormId +
                '}';
    }
}
