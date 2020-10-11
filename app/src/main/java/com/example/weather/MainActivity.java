package com.example.weather;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.weather.data.WeatherContract;
import com.example.weather.data.WeatherDbHelper;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    public static final String LOG_TAG = MainActivity.class.getName();
    private static String request_url = "https://api.openweathermap.org/data/2.5/weather?appid=fe21f6f759504260a7aa9a4c3b6a2492&lat=13.0266981&lon=77.5465897";
    protected LocationManager locationManager;
    protected Context context;
    FusedLocationProviderClient mFusedLocationClient;
    SwipeRefreshLayout refreshLayout;
    int PERMISSION_ID = 44;
    TextView date_view, temp, min, max, desc;

    public static void updateURL(String lat, String lon) {
        request_url += "&lat=" + lat + "&lon=" + lon;
        Log.v("MainActivity", "New url: " + request_url);
    }

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
        //net = findViewById(R.id.check_network);

        Log.v("MainActivity", "activity created");

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        /* if(updatedData(QueryUtils.getCurrentDate())) {
            Toast toast = Toast.makeText(MainActivity.this,
                    "ALready updated data for today",
                    Toast.LENGTH_LONG);
            desc.setText("Already updated for today");
            Log.v("MainActivity","Already updated data");

        } */
        //else {
        if (isNetworkAvailable()) {
            //EarthquakeAsyncTask task = new EarthquakeAsyncTask();
            //task.execute(REQUEST_URL);
            Log.v("MainActivity", "OLD URL: " + request_url);
            //updateURL(LAT, LON);
            Log.v("MainActivity", "NEW URL: " + request_url);

            new EarthquakeAsyncTask().execute(request_url);
        } else {

            Log.v("MainActivity", "No connection");
            Toast toast = Toast.makeText(getApplicationContext(),
                    "No internet connection.",
                    Toast.LENGTH_SHORT);

            toast.show();
        }

        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //getLastLocation();
                if (isNetworkAvailable()) {
                    //EarthquakeAsyncTask task = new EarthquakeAsyncTask();
                    //task.execute(REQUEST_URL);
                    Log.v("MainActivity", "OLD URL: " + request_url);
                    //updateURL(LAT, LON);
                    Log.v("MainActivity", "NEW URL: " + request_url);

                    new EarthquakeAsyncTask().execute(request_url);
                } else {


                    Log.v("MainActivity", "No connection");

                    Toast toast = Toast.makeText(getApplicationContext(),
                            "No internet connection.",
                            Toast.LENGTH_SHORT);

                    toast.show();
                }
                refreshLayout.setRefreshing(false);
            }
        });


    }

    private boolean updatedData(String currentDate) {
        String fetchedDate = "";

        WeatherDbHelper mDbHelper = new WeatherDbHelper(this);
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        String[] projection = {WeatherContract.WeatherEntry.COLUMN_DATE};

        Cursor cursor = null;              // The sort order

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

    public void insertData(String date, String max, String min, String summary) {

        WeatherDbHelper mDbHelper = new WeatherDbHelper(this);

        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(WeatherContract.WeatherEntry.COLUMN_DATE, date);
        values.put(WeatherContract.WeatherEntry.COLUMN_MAX_TEMP, max);
        values.put(WeatherContract.WeatherEntry.COLUMN_MIN_TEMP, min);
        values.put(WeatherContract.WeatherEntry.COLUMN_SUMMARY, summary);
        // Insert the new row, returning the primary key value of the new row
        long newRowId = db.insert(WeatherContract.WeatherEntry.TABLE_NAME, null, values);
    }

    private void displayDatabaseInfo() {
        // To access our database, we instantiate our subclass of SQLiteOpenHelper
        // and pass the context, which is the current activity.
        WeatherDbHelper mDbHelper = new WeatherDbHelper(this);

        // Create and/or open a database to read from it
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        // Perform this raw SQL query "SELECT * FROM weather"
        //Cursor cursor = db.rawQuery("SELECT * FROM " + WeatherContract.WeatherEntry.TABLE_NAME, null);

        // Define a projection that specifies which columns from the database
        // you will actually use after this query.
        String[] projection = {
                WeatherContract.WeatherEntry._ID,
                WeatherContract.WeatherEntry.COLUMN_DATE,
                WeatherContract.WeatherEntry.COLUMN_MAX_TEMP,
                WeatherContract.WeatherEntry.COLUMN_MIN_TEMP,
                WeatherContract.WeatherEntry.COLUMN_SUMMARY};

        // Perform a query on the weather table
        Cursor cursor = db.query(
                WeatherContract.WeatherEntry.TABLE_NAME,   // The table to query
                projection,            // The columns to return
                null,                  // The columns for the WHERE clause
                null,                  // The values for the WHERE clause
                null,                  // Don't group the rows
                null,                  // Don't filter by row groups
                null);                   // The sort order

        TextView displayView = (TextView) findViewById(R.id.date_view);

        try {

            //move cursor to latest position in database
            cursor.moveToPosition(cursor.getCount() - 1);

            // Figure out the index of each column
            int idColumnIndex = cursor.getColumnIndex(WeatherContract.WeatherEntry._ID);
            int dateColumnIndex = cursor.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_DATE);
            int maxColumnIndex = cursor.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_MAX_TEMP);
            int minColumnIndex = cursor.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_MIN_TEMP);
            int summaryColumnIndex = cursor.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_SUMMARY);

            String currentDate = cursor.getString(dateColumnIndex);
            String currentMax = cursor.getString(maxColumnIndex);
            String currentMin = cursor.getString(minColumnIndex);
            String currentSummary = cursor.getString(summaryColumnIndex);

            SimpleDateFormat string_to_date = new SimpleDateFormat("dd-MM-yyyy");
            Date date = null;
            try {
                date = string_to_date.parse(currentDate);
            } catch (ParseException e) {
                e.printStackTrace();
                Log.v("MainActivity", "Date not parsed successfully");
            }

            SimpleDateFormat date_to_string = new SimpleDateFormat("EEE, dd MMM, yyyy");
            desc.setText(currentSummary);
            min.setText(currentMin);
            max.setText(currentMax);
            date_view.setText(date_to_string.format(date));

            // Iterate through all the returned rows in the cursor
            /*while (cursor.moveToNext()) {
                // Use that index to extract the String or Int value of the word
                // at the current row the cursor is on.
                int currentID = cursor.getInt(idColumnIndex);
                String currentDate = cursor.getString(dateColumnIndex);
                String currentMax = cursor.getString(maxColumnIndex);
                String currentMin = cursor.getString(minColumnIndex);
                String currentSummary = cursor.getString(summaryColumnIndex);
                // Display the values from each column of the current row in the cursor in the TextView
                displayView.append(("\n" + currentID + " - " +
                        currentDate + " - " +
                        currentMax + " - " +
                        currentMin + " - " +
                        currentSummary));
            }*/

        } finally {
            // Always close the cursor when you're done reading from it. This releases all its
            // resources and makes it invalid.
            cursor.close();
        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private class EarthquakeAsyncTask extends AsyncTask<String, Void, WeatherData> {

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
                //weather_data_container.setVisibility(View.VISIBLE);
                //net.setVisibility(View.GONE);
                /* desc.setText(weather.getDay_summary());
                min.setText("Min: "+weather.getMin_temp());
                max.setText("Max: "+weather.getMax_temp());
                lat.setText("Latitude: "+weather.getLat());
                lon.setText("Longitude: "+weather.getLon()); */
                insertData(weather.getDate(), weather.getMax_temp(), weather.getMin_temp(), weather.getDay_summary());
                displayDatabaseInfo();
            }

        }
    }

}