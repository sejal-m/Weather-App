package com.example.weather;

import java.io.Serializable;

public class WeatherData implements Serializable {

    double lat, lon;
    double location_id;
    String date;
    double wind_speed,humidity,precipitation;
    String min_temp, max_temp, visibility_worst;
    String sunrise, sunset;
    String day_summary;

    public WeatherData(double lon, double lat, String min_temp, String max_temp, String day_summary) {
        this.lat = lat;
        this.lon = lon;
        this.min_temp = min_temp;
        this.max_temp = max_temp;
        this.day_summary = day_summary;
    }

    public WeatherData(double lat, double lon, double location_id, String date, double wind_speed, double humidity, double precipitation, String min_temp, String max_temp, String visibility_worst, String sunrise, String sunset, String day_summary) {
        this.lat = lat;
        this.lon = lon;
        this.location_id = location_id;
        this.date = date;
        this.wind_speed = wind_speed;
        this.humidity = humidity;
        this.precipitation = precipitation;
        this.min_temp = min_temp;
        this.max_temp = max_temp;
        this.visibility_worst = visibility_worst;
        this.sunrise = sunrise;
        this.sunset = sunset;
        this.day_summary = day_summary;
    }

    public double getLat() {
        return lat;
    }

    public double getLon() {
        return lon;
    }

    public double getLocation_id() {
        return location_id;
    }

    public String getDate() {
        return date;
    }

    public double getWind_speed() {
        return wind_speed;
    }

    public double getHumidity() {
        return humidity;
    }

    public double getPrecipitation() {
        return precipitation;
    }

    public String getMin_temp() {
        return min_temp;
    }

    public String getMax_temp() {
        return max_temp;
    }

    public String getVisibility_worst() {
        return visibility_worst;
    }

    public String getSunrise() {
        return sunrise;
    }

    public String getSunset() {
        return sunset;
    }

    public String getDay_summary() {
        return day_summary;
    }
}

