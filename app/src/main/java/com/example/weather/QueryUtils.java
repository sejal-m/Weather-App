package com.example.weather;

import android.text.TextUtils;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import static com.example.weather.SearchActivity.LOG_TAG;

public final class QueryUtils {


    private QueryUtils() {
    }

    public static WeatherData extractJSON(String weatherJSON) {

        if (TextUtils.isEmpty(weatherJSON)) {
            return null;
        }

        WeatherData weatherObject = null;

        try {
            JSONObject baseJsonResponse = new JSONObject(weatherJSON);
            JSONObject weather = baseJsonResponse.getJSONArray("weather").getJSONObject(0);
            JSONObject main = baseJsonResponse.getJSONObject("main");
            JSONObject wind = baseJsonResponse.getJSONObject("wind");
            JSONObject sys = baseJsonResponse.getJSONObject("sys");
            long date = baseJsonResponse.getLong("dt");
            String description = weather.getString("description");
            int weather_code = weather.getInt("id");
            double temp = main.getDouble("temp");
            double tempMin = main.getDouble("temp_min");
            double tempMax = main.getDouble("temp_max");
            double humidity = main.getDouble("humidity");
            double pressure = main.getDouble("pressure");
            double visibility = baseJsonResponse.getDouble("visibility");
            double wind_speed = wind.getDouble("speed");
            long sunrise = sys.getLong("sunrise");
            long sunset = sys.getLong("sunset");
            weatherObject = new WeatherData(epochToDay(date), kelvinToCelsius(tempMin), kelvinToCelsius(tempMax), kelvinToCelsius(temp), wind_speed, humidity, pressure, epochToTime(sunrise), epochToTime(sunset), visibility, weather_code, description);
        } catch (JSONException e) {
            Log.e("QueryUtils", "Problem parsing JSON ", e);
        }
        return weatherObject;
    }

    private static String epochToDay(long timestamp) {
        Date d = new Date(timestamp * 1000L);
        SimpleDateFormat formatted = new SimpleDateFormat("EEE, MMM dd, yyyy ");
        return formatted.format(d);
    }

    private static String epochToTime(long timestamp) {
        Date d = new Date(timestamp * 1000L);
        SimpleDateFormat formatted = new SimpleDateFormat("hh:mm aaa");
        formatted.setTimeZone(TimeZone.getTimeZone("IST"));
        return formatted.format(d);
    }

    private static double kelvinToCelsius(double temp) {
        temp -= 273.15;
        return temp;
    }

    public static WeatherData fetchWeatherData(String requestUrl) {

        URL url = createUrl(requestUrl);

        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem making the HTTP request.", e);
        }

        return extractJSON(jsonResponse);
    }

    private static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Problem creating the URL ", e);
        }
        return url;
    }

    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";
        if (url == null) {
            return jsonResponse;
        }
        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000);
            urlConnection.setConnectTimeout(15000);
            urlConnection.setRequestMethod("GET");

            urlConnection.connect();

            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
            }
        }

        catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving the weather JSON results.", e);
        }

        finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    private static String readFromStream(InputStream inputStream) throws IOException {

        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }
}
