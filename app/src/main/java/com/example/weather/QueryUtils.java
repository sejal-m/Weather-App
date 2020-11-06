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
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import static com.example.weather.MainActivity.LOG_TAG;

public final class QueryUtils {

    private static final String SAMPLE_JSON_RESPONSE = "{\"coord\":{\"lon\":77.6,\"lat\":12.98},\"weather\":[{\"id\":802,\"main\":\"Clouds\",\"description\":\"scattered clouds\",\"icon\":\"03d\"}],\"base\":\"stations\",\"main\":"+
            "{\"temp\":301.31,\"feels_like\":304.77,\"temp_min\":300.37,\"temp_max\":302.59,\"pressure\":1012,\"humidity\":65},\"visibility\":6000,\"wind\":{\"speed\":1,\"deg\":0},\"clouds\":"+
            "{\"all\":40},\"dt\":1595398053,\"sys\":{\"type\":1,\"id\":9205,\"country\":\"IN\",\"sunrise\":1595377965,\"sunset\":1595423941},\"timezone\":19800,\"id\":1277333,\"name\":\"Bengaluru\",\"cod\":200}";

    private QueryUtils() {
    }

    public static WeatherData extractJSON(String weatherJSON) {

        if (TextUtils.isEmpty(weatherJSON)) {
            return null;
        }

        WeatherData weatherObject = null;

        try {
            JSONObject baseJsonResponse = new JSONObject(weatherJSON);
            String date = baseJsonResponse.getString("date");
            JSONObject data = baseJsonResponse.getJSONObject("data");
            double temp_min = data.getDouble("temp_min");
            double temp_max = data.getDouble("temp_max");
            double humidity = data.getDouble("humidity");
            double wind_speed = data.getDouble("wind_speed");
            double wind_direction = data.getDouble("wind_direction");
            double wind_gust = data.getDouble("wind_gust");
            double prec = data.getDouble("precipitation");
            String prec_type = data.getString("precipitation_type");
            double prec_prob = data.getDouble("precipitation_probability");
            String sunrise = data.getString("sunrise");
            String sunset = data.getString("sunset");
            double visibility = data.getDouble("visibility");
            String weather_code = data.getString("weather_code");
            weatherObject = new WeatherData(date,temp_min,temp_max, humidity, wind_speed, wind_direction, wind_gust, prec, prec_type, prec_prob, visibility, sunrise, sunset, weather_code);
        }
        catch (JSONException e) {
            Log.e("QueryUtils", "Problem parsing JSON ", e);
        }
        return weatherObject;
    }

    public static String getCurrentDate() {
        Date date = new Date();
        SimpleDateFormat dateFormatter = new SimpleDateFormat("dd-MM-yyyy");
        return dateFormatter.format(date);
    }

    private static String epochToDay(long timestamp) {
        Date d = new Date(timestamp * 1000L);
        SimpleDateFormat formatted = new SimpleDateFormat("dd-MM-yyyy");
        formatted.setTimeZone(TimeZone.getTimeZone("Etc/UTC"));
        return formatted.format(d);
    }

    private static String kelvinToCelsius(double temp) {
        temp -= 273.15;
        DecimalFormat temperatureFormat = new DecimalFormat("0.00");
        return temperatureFormat.format(temp)+"°";
    }

    public static WeatherData fetchWeatherData(String requestUrl) {

        URL url = createUrl(requestUrl);


        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem making the HTTP request.", e);
        }


        WeatherData extractedData = extractJSON(jsonResponse);


        return extractedData;
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
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving the weather JSON results.", e);
        } finally {
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
