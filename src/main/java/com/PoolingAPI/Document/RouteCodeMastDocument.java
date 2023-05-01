package com.PoolingAPI.Document;

import org.springframework.data.mongodb.core.mapping.Document;

import java.sql.Timestamp;

@Document
public class RouteCodeMastDocument {

    private String RouteCode;
    private String Description;
    private String SystemicRouteIndicator;
    private String DescriptionF;
    private Timestamp insertTime;
    private String PharmacyId;

    public String getRouteCode() {
        return RouteCode;
    }

    public void setRouteCode(String routeCode) {
        RouteCode = routeCode;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public String getSystemicRouteIndicator() {
        return SystemicRouteIndicator;
    }

    public void setSystemicRouteIndicator(String systemicRouteIndicator) {
        SystemicRouteIndicator = systemicRouteIndicator;
    }

    public String getDescriptionF() {
        return DescriptionF;
    }

    public void setDescriptionF(String descriptionF) {
        DescriptionF = descriptionF;
    }

    public Timestamp getInsertTime() {
        return insertTime;
    }

    public void setInsertTime(Timestamp insertTime) {
        this.insertTime = insertTime;
    }

    public String getPharmacyId() {
        return PharmacyId;
    }

    public void setPharmacyId(String pharmacyId) {
        PharmacyId = pharmacyId;
    }
}
