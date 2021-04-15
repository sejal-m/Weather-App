package com.example.weather;

import java.io.Serializable;

public class WeatherData implements Serializable {

    String city;
    String date;
    double min_temp, max_temp, temp;
    double wind_speed;
    double humidity;
    double pressure;
    String sunrise, sunset;
    double visibility;
    int weather_code;
    String description;

    public WeatherData(String date, double min_temp, double max_temp, double temp, double wind_speed, double humidity, double pressure, String sunrise, String sunset, double visibility, int weather_code, String description) {
        this.city = city;
        this.date = date;
        this.min_temp = min_temp;
        this.max_temp = max_temp;
        this.temp = temp;
        this.wind_speed = wind_speed;
        this.humidity = humidity;
        this.pressure = pressure;
        this.sunrise = sunrise;
        this.sunset = sunset;
        this.visibility = visibility;
        this.weather_code = weather_code;
        this.description = description;
    }

    public WeatherData(String date, double min_temp, double max_temp, double temp, double humidity, String description) {
        this.date = date;
        this.min_temp = min_temp;
        this.max_temp = max_temp;
        this.temp = temp;
        this.humidity = humidity;
        this.description = description;
    }

    public String getCity() {
        return city;
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

    public double getTemp() {
        return temp;
    }

    public double getWind_speed() {
        return wind_speed;
    }

    public double getHumidity() {
        return humidity;
    }

    public double getPressure() {
        return pressure;
    }

    public String getSunrise() {
        return sunrise;
    }

    public String getSunset() {
        return sunset;
    }

    public double getVisibility() {
        return visibility;
    }

    public int getWeather_code() {
        return weather_code;
    }

    public String getDescription() {
        return description;
    }
}

