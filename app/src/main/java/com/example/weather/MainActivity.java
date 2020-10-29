package com.example.weather;

import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.weather.data.WeatherContract;
import com.example.weather.data.WeatherDbHelper;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    Dialog myDialog;
    public static final String LOG_TAG = MainActivity.class.getName();
    //private static String request_url = "https://api.openweathermap.org/data/2.5/weather?appid=fe21f6f759504260a7aa9a4c3b6a2492&lat=13.0266981&lon=77.5465897";
    private static String request_url = "http://3.0.50.164/weather/forecast.php";
    SwipeRefreshLayout refreshLayout;
    TextView date_view, temp, min, max, desc;
    TextView prec_probability;
    TableLayout detailed_forecast;
    LinearLayout base_container;
    private BottomSheetBehavior mBottomSheetBehavior;
    private int updated = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        desc = findViewById(R.id.desc);
        min = findViewById(R.id.temp_min);
        max = findViewById(R.id.temp_max);
        date_view = findViewById(R.id.date_view);
        temp = findViewById(R.id.temp);
        refreshLayout = findViewById(R.id.pullToRefresh);
        base_container = findViewById(R.id.base_container);
        prec_probability = findViewById(R.id.prec_probability);
        detailed_forecast = findViewById(R.id.detailed_forecast);
        myDialog = new Dialog(this);
        View bottomSheet = findViewById(R.id.bottom_sheet);
        mBottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
        mBottomSheetBehavior.setHideable(false);

        mBottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                switch (newState) {
                    case BottomSheetBehavior.STATE_COLLAPSED:
                    case BottomSheetBehavior.STATE_SETTLING:
                        //prec_probability.setVisibility(View.VISIBLE);
                        //detailed_forecast.setVisibility(View.INVISIBLE);
                        prec_probability.setText("50% chance of precipitation today.");
                        prec_probability.setBackgroundColor(Color.parseColor("#4D1F9F"));
                        break;
                    case BottomSheetBehavior.STATE_DRAGGING: case BottomSheetBehavior.STATE_EXPANDED:
                        //prec_probability.setVisibility(View.INVISIBLE);
                        //detailed_forecast.setVisibility(View.VISIBLE);
                        prec_probability.setText("Weather Details");
                        prec_probability.setBackgroundColor(Color.parseColor("#ffffff"));
                        break;
                }
            }
            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                //sample.setText("Sliding...");
            }
        });

        updateBackground();

        if(updated != 0 && updatedData(QueryUtils.getCurrentDate())) {
            Toast toast = Toast.makeText(MainActivity.this,
                    "ALready updated data for today",
                    Toast.LENGTH_LONG);
            Log.v("MainActivity","Displaying old data");
            displayDatabaseInfo();
            //new WeatherAsyncTask().execute(request_url);
        }
        else {
            if (isNetworkAvailable()) {
                //EarthquakeAsyncTask task = new EarthquakeAsyncTask();
                //task.execute(REQUEST_URL);
                Log.v("MainActivity", "OLD URL: " + request_url);
                //updateURL(LAT, LON);
                Log.v("MainActivity", "NEW URL: " + request_url);
                new WeatherAsyncTask().execute(request_url);
            } else {
                Log.v("MainActivity", "No connection");
                Toast toast = Toast.makeText(getApplicationContext(),
                        "No internet connection.",
                        Toast.LENGTH_SHORT);
                toast.show();
                ShowPopup();
            }
        }

        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //getLastLocation();
                    if (isNetworkAvailable()) {
                        //EarthquakeAsyncTask task = new EarthquakeAsyncTask();
                        Log.v("MainActivity", "data updated on refreshing");
                        new WeatherAsyncTask().execute(request_url);
                    } else {
                        Log.v("MainActivity", "No connection");
                        Toast toast = Toast.makeText(getApplicationContext(),
                                "No internet connection.",
                                Toast.LENGTH_SHORT);
                        toast.show();
                        ShowPopup();
                    }
                refreshLayout.setRefreshing(false);
            }
        });


    }

    public void ShowPopup() {
        myDialog.setContentView(R.layout.popup_view);
        myDialog.show();
        myDialog.setCanceledOnTouchOutside(true);
    }

    private void updateBackground() {
        Calendar c = Calendar.getInstance();
        int timeOfDay = c.get(Calendar.HOUR_OF_DAY);

        if(timeOfDay >= 0 && timeOfDay < 12){
            base_container.setBackgroundResource(R.drawable.day_wallpaper);
        }else if(timeOfDay >= 12 && timeOfDay < 24){
            base_container.setBackgroundResource(R.drawable.evening_wallpaper);
        }
    }

    private boolean updatedData(String currentDate) {
        updated = 1;
        String fetchedDate = "";

        WeatherDbHelper mDbHelper = new WeatherDbHelper(this);
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        String[] projection = {WeatherContract.WeatherEntry.COLUMN_DATE};

        Cursor cursor = null;
        try {
            cursor = db.query(
                    WeatherContract.WeatherEntry.TABLE_NAME,   // The table to query
                    projection,            // The columns to return
                    null,                  // The columns for the WHERE clause
                    null,                  // The values for the WHERE clause
                    null,                  // Don't group the rows
                    null,                  // Don't filter by row groups
                    null);
            cursor.moveToPosition(cursor.getCount() - 1);
            int dateColumnIndex = cursor.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_DATE);
            //while (cursor.moveToNext())
            fetchedDate = cursor.getString(dateColumnIndex);
        } finally {
            cursor.close();
        }
        Log.v("MainActivity", currentDate.equals(fetchedDate) + "");
        return currentDate.equals(fetchedDate);
    }

    public void insertData(WeatherData weather) {

        WeatherDbHelper mDbHelper = new WeatherDbHelper(this);

        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(WeatherContract.WeatherEntry.COLUMN_DATE, weather.getDate());
        values.put(WeatherContract.WeatherEntry.COLUMN_MAX_TEMP, weather.getMax_temp());
        values.put(WeatherContract.WeatherEntry.COLUMN_MIN_TEMP, weather.getMin_temp());
        values.put(WeatherContract.WeatherEntry.COLUMN_HUMIDITY, weather.getHumidity());
        values.put(WeatherContract.WeatherEntry.COLUMN_WIND_SPEED, weather.getWind_speed());
        values.put(WeatherContract.WeatherEntry.COLUMN_WIND_DIRECTION, weather.getWind_direction());
        values.put(WeatherContract.WeatherEntry.COLUMN_WIND_GUST, weather.getWind_gust());
        values.put(WeatherContract.WeatherEntry.COLUMN_PRECIPITATION, weather.getPrecipitation());
        values.put(WeatherContract.WeatherEntry.COLUMN_PRECIPITATION_TYPE, weather.getPrecipitation_type());
        values.put(WeatherContract.WeatherEntry.COLUMN_PRECIPITATION_PROBABILITY, weather.getPrecipitation_probability());
        values.put(WeatherContract.WeatherEntry.COLUMN_SUNRISE, weather.getSunrise());
        values.put(WeatherContract.WeatherEntry.COLUMN_SUNSET, weather.getSunset());
        values.put(WeatherContract.WeatherEntry.COLUMN_VISIBILITY, weather.getVisibility());
        values.put(WeatherContract.WeatherEntry.COLUMN_SUMMARY, weather.getWeather_code());
        // Insert the new row, returning the primary key value of the new row
        db.insert(WeatherContract.WeatherEntry.TABLE_NAME, null, values);
    }

    private void displayDatabaseInfo() {
        WeatherDbHelper mDbHelper = new WeatherDbHelper(this);
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        String[] projection = {
                WeatherContract.WeatherEntry._ID,
                WeatherContract.WeatherEntry.COLUMN_DATE,
                WeatherContract.WeatherEntry.COLUMN_MAX_TEMP,
                WeatherContract.WeatherEntry.COLUMN_MIN_TEMP,
                WeatherContract.WeatherEntry.COLUMN_PRECIPITATION_PROBABILITY,
                WeatherContract.WeatherEntry.COLUMN_SUMMARY};
        Cursor cursor = db.query(
                WeatherContract.WeatherEntry.TABLE_NAME,   // The table to query
                projection,            // The columns to return
                null,                  // The columns for the WHERE clause
                null,                  // The values for the WHERE clause
                null,                  // Don't group the rows
                null,                  // Don't filter by row groups
                null);                   // The sort order

        try {
            cursor.moveToPosition(cursor.getCount() - 1);
            int dateColumnIndex = cursor.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_DATE);
            int maxColumnIndex = cursor.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_MAX_TEMP);
            int minColumnIndex = cursor.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_MIN_TEMP);
            int prec_probabilityColumnIndex = cursor.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_PRECIPITATION_PROBABILITY);
            int summaryColumnIndex = cursor.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_SUMMARY);

            String currentDate = cursor.getString(dateColumnIndex);
            double currentMax = cursor.getDouble(maxColumnIndex);
            double currentMin = cursor.getDouble(minColumnIndex);
            String currentPrecProbability = cursor.getString(prec_probabilityColumnIndex);
            String currentSummary = cursor.getString(summaryColumnIndex);

            SimpleDateFormat string_to_date = new SimpleDateFormat("yyyy-MM-dd");
            Date date = null;
            try {
                date = string_to_date.parse(currentDate);
            } catch (ParseException e) {
                e.printStackTrace();
                Log.v("MainActivity", "Date not parsed successfully");
            }

            SimpleDateFormat date_to_string = new SimpleDateFormat("EEE, dd MMM, yyyy");

            desc.setText(currentSummary);
            min.setText("min "+currentMin+"°");
            max.setText("max "+currentMax+"°");
            prec_probability.setText(currentPrecProbability+ "% chance of precipitation today");
            temp.setText(Integer.toString((int) (( currentMin + currentMax ) / 2)));
            date_view.setText(date_to_string.format(date));

        } finally {
            cursor.close();
        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private class WeatherAsyncTask extends AsyncTask<String, Void, WeatherData> {

        @Override
        protected WeatherData doInBackground(String... urls) {

            if (urls.length < 1 || urls[0] == null) {
                return null;
            }

            WeatherData result = QueryUtils.fetchWeatherData(urls[0]);
            return result;
        }

        @Override
        protected void onPostExecute(WeatherData weather) {
            if (weather != null) {
                insertData(weather);
                displayDatabaseInfo();
            }

        }
    }

}