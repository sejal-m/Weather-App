package com.example.weather;

import java.io.Serializable;

public class WeatherData implements Serializable {

    double lat, lon;
    double location_id;
    String date;
    double min_temp, max_temp, humidity;
    double wind_speed, wind_direction, wind_gust;
    double precipitation, precipitation_probability;
    String precipitation_type;
    double visibility;
    String sunrise, sunset;
    String weather_code;

    public WeatherData(String date, double min_temp, double max_temp, double humidity, double wind_speed, double wind_direction, double wind_gust, double precipitation, String precipitation_type, double precipitation_probability,  double visibility, String sunrise, String sunset, String weather_code) {
        this.date = date;
        this.min_temp = min_temp;
        this.max_temp = max_temp;
        this.humidity = humidity;
        this.wind_speed = wind_speed;
        this.wind_direction = wind_direction;
        this.wind_gust = wind_gust;
        this.precipitation = precipitation;
        this.precipitation_probability = precipitation_probability;
        this.precipitation_type = precipitation_type;
        this.visibility = visibility;
        this.sunrise = sunrise;
        this.sunset = sunset;
        this.weather_code = weather_code;
    }

    public WeatherData(String date, double min_temp, double max_temp, double precipitation_probability, String weather_code) {
        this.date = date;
        this.min_temp = min_temp;
        this.max_temp = max_temp;
        this.precipitation_probability = precipitation_probability;
        this.weather_code = weather_code;
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

    public double getMin_temp() {
        return min_temp;
    }

    public double getMax_temp() {
        return max_temp;
    }

    public double getWind_speed() {
        return wind_speed;
    }

    public double getWind_direction() {
        return wind_direction;
    }

    public double getWind_gust() {
        return wind_gust;
    }

    public double getPrecipitation() {
        return precipitation;
    }

    public String getPrecipitation_type() {
        return precipitation_type;
    }

    public double getPrecipitation_probability() {
        return precipitation_probability;
    }

    public double getVisibility() {
        return visibility;
    }

    public String getSunrise() {
        return sunrise;
    }

    public String getSunset() {
        return sunset;
    }

    public String getWeather_code() {
        return weather_code;
    }

    public double getHumidity() {
        return humidity;
    }
}

