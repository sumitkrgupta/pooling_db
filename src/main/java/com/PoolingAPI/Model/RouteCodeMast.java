package com.PoolingAPI.Model;

import org.springframework.stereotype.Component;

@Component
public class RouteCodeMast {

    private String RouteCode;
    private String Description;
    private String SystemicRouteIndicator;
    private String DescriptionF;

    public RouteCodeMast() {
        super();
    }

    public RouteCodeMast(String routeCode, String description, String systemicRouteIndicator, String descriptionF) {
        RouteCode = routeCode;
        Description = description;
        SystemicRouteIndicator = systemicRouteIndicator;
        DescriptionF = descriptionF;
    }

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
}
