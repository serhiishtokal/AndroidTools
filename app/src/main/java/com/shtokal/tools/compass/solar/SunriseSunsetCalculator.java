package com.shtokal.tools.compass.solar;

import java.util.Calendar;
import java.util.TimeZone;

public class SunriseSunsetCalculator {

    private SolarEventCalculator calculator;

    public SunriseSunsetCalculator(Location location, TimeZone timeZone) {
        this.calculator = new SolarEventCalculator(location, timeZone);
    }

    public Calendar getOfficialSunriseCalendarForDate(Calendar date) {
        return calculator.computeSunriseCalendar(Zenith.OFFICIAL, date);
    }

    public Calendar getOfficialSunsetCalendarForDate(Calendar date) {
        return calculator.computeSunsetCalendar(Zenith.OFFICIAL, date);
    }

}
