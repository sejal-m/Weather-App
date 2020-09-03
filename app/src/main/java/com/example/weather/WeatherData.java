package com.example.weather;

import java.io.Serializable;

public class WeatherData implements Serializable {

    double lat, lon;
    String minTemp, maxTemp;
    String description;
    String date;

    public WeatherData(double lat, double lon,String minTemp, String maxTemp, String description, String time) {
        this.lat = lat;
        this.lon = lon;
        this.minTemp = minTemp;
        this.maxTemp = maxTemp;
        this.description = description;
        this.date = time;
    }

    public double getLat() {
        return lat;
    }

    public double getLon() {
        return lon;
    }

    public String getMinTemp() {
        return minTemp;
    }

    public String getMaxTemp() {
        return maxTemp;
    }

    public String getDescription() {
        return description;
    }

    public String getTimestamp() {
        return date;
    }
}

