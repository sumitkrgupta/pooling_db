package com.PoolingAPI.Model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import javax.validation.constraints.NotNull;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "pharmacyID"
})
public class RequestFormat {

    @JsonProperty("pharmacyID")
    @NotNull
    private String pharmacyID;

    public String getPharmacyID() {
        return pharmacyID;
    }

    public void setPharmacyID(String pharmacyID) {
        this.pharmacyID = pharmacyID;
    }

    @Override
    public String toString() {
        return "DrugRequestFormat{" +
                "pharmacyID='" + pharmacyID + '\'' +
                '}';
    }
}
