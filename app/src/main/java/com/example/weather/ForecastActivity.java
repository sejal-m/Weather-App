package com.example.weather;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;


public class ForecastActivity extends AppCompatActivity {

    private static String request_url = "https://api.openweathermap.org/data/2.5/onecall/timemachine?lat=13.03&lon=77.55&appid=fe21f6f759504260a7aa9a4c3b6a2492";
    String today;
    TextView description;
    ArrayList<Long> days = new ArrayList<Long>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forecast);

        description = findViewById(R.id.text);

        today = QueryUtils.getCurrentDate();
        for(int i = 0; i < 5; i++) {
            LocalDate date = LocalDate.now().minusDays(i);
            String daysAgo = date.toString();
            //ZoneId zoneId = ZoneId.systemDefault();  or: ZoneId.of("Europe/Oslo");
            ZoneId zoneId = ZoneId.of("Asia/Kolkata");
            long epoch = date.atStartOfDay(zoneId).toEpochSecond();
            days.add(epoch);
            Log.v("ForecastActivity", days.get(i)+" "+epoch);
        }

        if(isNetworkAvailable()) {
            for(int i = 0; i < 5; i++) {
                String new_url = request_url+"&dt="+days.get(i);
                Log.v("New url", new_url);
                new ForecastActivity.ForecastAsyncTask().execute(new_url);
            }
            Toast.makeText(ForecastActivity.this, "Connection Available", Toast.LENGTH_LONG).show();
        }
        else {
            Toast.makeText(ForecastActivity.this, "Connection Not Available",Toast.LENGTH_LONG).show();
        }

    }
    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private class ForecastAsyncTask extends AsyncTask<String, Void, WeatherData> {

        @Override
        protected WeatherData doInBackground(String... urls) {

            if (urls.length < 1 || urls[0] == null) {
                return null;
            }

            WeatherData result = QueryUtils.fetchWeatherData(urls[0],1);
            //WriteObjectToFile(result);
            return result;
        }

        @Override
        protected void onPostExecute(WeatherData weather) {
            //weather = ReadObjectFromFile(FILE_NAME);
            Toast toast = null;
            if(weather != null) {
                //weather_data_container.setVisibility(View.VISIBLE);
                //net.setVisibility(View.GONE);
                //displayWeatherData(weather);
                description.append(weather.getTimestamp());
                toast.makeText(ForecastActivity.this , "Description: "+weather.getTimestamp(), Toast.LENGTH_LONG).show();
            }
        }
    }
}