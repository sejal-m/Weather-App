package com.example.weather;

public class WeatherData {

    double lat, lon;
    String minTemp, maxTemp;
    String description;

    public WeatherData(double lat, double lon,String minTemp, String maxTemp, String description) {
        this.lat = lat;
        this.lon = lon;
        this.minTemp = minTemp;
        this.maxTemp = maxTemp;
        this.description = description;
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
}
