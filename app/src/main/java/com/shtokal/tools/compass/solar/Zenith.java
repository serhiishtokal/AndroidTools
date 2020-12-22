package com.shtokal.tools.compass.solar;

import java.math.BigDecimal;

public class Zenith {

    public static final Zenith OFFICIAL = new Zenith(90.8333);

    private final BigDecimal degrees;

    public Zenith(double degrees) {
        this.degrees = BigDecimal.valueOf(degrees);
    }

    public BigDecimal degrees() {
        return degrees;
    }
}
