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
            JSONObject coord = baseJsonResponse.getJSONObject("coord");
            long timestamp = baseJsonResponse.getLong("dt");
            double lon = coord.getDouble("lon");
            double lat = coord.getDouble("lat");
            String description = weather.getString("description");
            double tempMin = main.getDouble("temp_min");
            double tempMax = main.getDouble("temp_max");
            Log.v("MainActivity", "lon = "+lon+" ,lat = "+lat);
            weatherObject = new WeatherData(lon, lat, kelvinToCelsius(tempMin), kelvinToCelsius(tempMax), description);
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

    private static double kelvinToCelsius(double temp) {
        temp -= 273.15;
        return temp;
        //DecimalFormat temperatureFormat = new DecimalFormat("0.00");
        //return temperatureFormat.format(temp)+"°";
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
