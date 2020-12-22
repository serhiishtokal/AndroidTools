package com.shtokal.tools.compass.solar;

import java.math.BigDecimal;

public class Location {

    private BigDecimal latitude;
    private BigDecimal longitude;




    public Location(double latitude, double longitude) {
        this.latitude = new BigDecimal(latitude);
        this.longitude = new BigDecimal(longitude);
    }


    public BigDecimal getLatitude() {
        return latitude;
    }


    public BigDecimal getLongitude() {
        return longitude;
    }
}
