package com.example.weather;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.weather.data.WeatherContract;
import com.example.weather.data.WeatherDbHelper;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

public class MainActivity extends AppCompatActivity {
    int number = 0;
    FusedLocationProviderClient mFusedLocationClient;
    SwipeRefreshLayout refreshLayout;
    int PERMISSION_ID = 44;

    TextView desc, min, max, lat, lon, net, sample;
    LinearLayout weather_data_container;
    protected LocationManager locationManager;
    String LAT = "sample", LON = "sample";
    protected Context context;

    public static final String LOG_TAG = MainActivity.class.getName();

    private static String request_url = "https://api.openweathermap.org/data/2.5/weather?appid=fe21f6f759504260a7aa9a4c3b6a2492&lat=13.0266981&lon=77.5465897";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        desc = findViewById(R.id.desc);
        min = findViewById(R.id.min);
        max = findViewById(R.id.max);
        lat = findViewById(R.id.lat);
        lon = findViewById(R.id.lon);
        refreshLayout = findViewById(R.id.pullToRefresh);
        net = findViewById(R.id.check_network);
        weather_data_container = findViewById(R.id.weather_data_container);

        Log.v("MainActivity", "activity created");

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        if(updatedData(QueryUtils.getCurrentDate())) {
            Toast toast = Toast.makeText(MainActivity.this,
                    "ALready updated data for today",
                    Toast.LENGTH_LONG);
            Log.v("MainActivity","Already updated data");

        }
        else {
            if(isNetworkAvailable()) {
                //EarthquakeAsyncTask task = new EarthquakeAsyncTask();
                //task.execute(REQUEST_URL);
                Log.v("MainActivity", "OLD URL: "+request_url);
                //updateURL(LAT, LON);
                Log.v("MainActivity", "NEW URL: "+request_url);
                weather_data_container.setVisibility(View.VISIBLE );
                net.setVisibility(View.INVISIBLE);
                new EarthquakeAsyncTask().execute(request_url);
            }

            else {
                weather_data_container.setVisibility(View.INVISIBLE );
                net.setVisibility(View.VISIBLE);
                Log.v("MainActivity", "No connection");
                net.setText("No connection");
                Toast toast = Toast.makeText(getApplicationContext(),
                        "No internet connection.",
                        Toast.LENGTH_SHORT);

                toast.show();
            }
        }
        //getLastLocation();

        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //getLastLocation();
                if(updatedData(QueryUtils.getCurrentDate())) {
                    Toast toast = Toast.makeText(getApplicationContext(),
                            "ALready updated data for today",
                            Toast.LENGTH_LONG);
                    Log.v("MainActivity","Already updated data");
                }
                else {
                    if(isNetworkAvailable()) {
                        //EarthquakeAsyncTask task = new EarthquakeAsyncTask();
                        //task.execute(REQUEST_URL);
                        Log.v("MainActivity", "OLD URL: "+request_url);
                        //updateURL(LAT, LON);
                        Log.v("MainActivity", "NEW URL: "+request_url);
                        weather_data_container.setVisibility(View.VISIBLE );
                        net.setVisibility(View.INVISIBLE);
                        new EarthquakeAsyncTask().execute(request_url);
                    }

                    else {
                        weather_data_container.setVisibility(View.INVISIBLE );
                        net.setVisibility(View.VISIBLE);
                        Log.v("MainActivity", "No connection");
                        net.setText("No connection");
                        Toast toast = Toast.makeText(getApplicationContext(),
                                "No internet connection.",
                                Toast.LENGTH_SHORT);

                        toast.show();
                    }
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

        Cursor cursor = db.query(
                WeatherContract.WeatherEntry.TABLE_NAME,   // The table to query
                projection,            // The columns to return
                null,                  // The columns for the WHERE clause
                null,                  // The values for the WHERE clause
                null,                  // Don't group the rows
                null,                  // Don't filter by row groups
                null);                   // The sort order

        try {
            int dateColumnIndex = cursor.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_DATE);
            while (cursor.moveToNext())
                fetchedDate = cursor.getString(dateColumnIndex);
        }
        finally {
            cursor.close();
        }
        Log.v("MainActivity",currentDate.equals(fetchedDate)+"");
        return currentDate.equals(fetchedDate);
    }

    /*@SuppressLint("MissingPermission")
    private void getLastLocation(){
        if (checkPermissions()) {
            if (isLocationEnabled()) {
                mFusedLocationClient.getLastLocation().addOnCompleteListener(
                        new OnCompleteListener<Location>() {
                            @Override
                            public void onComplete(@NonNull Task<Location> task) {
                                Location location = task.getResult();
                                if (location == null) {
                                    requestNewLocationData();
                                } else {

                                    LAT = location.getLatitude()+"";
                                    LON = location.getLongitude()+"";
                                    //String sample = request_url;

                                    //new weatherTask().execute();
                                    if(isNetworkAvailable()) {
                                        //EarthquakeAsyncTask task = new EarthquakeAsyncTask();
                                        //task.execute(REQUEST_URL);
                                        Log.v("MainActivity", "OLD URL: "+request_url);
                                        updateURL(LAT, LON);
                                        Log.v("MainActivity", "NEW URL: "+request_url);
                                        weather_data_container.setVisibility(View.VISIBLE );
                                        net.setVisibility(View.INVISIBLE);
                                        new EarthquakeAsyncTask().execute(request_url);
                                    }

                                    else {
                                        weather_data_container.setVisibility(View.INVISIBLE );
                                        net.setVisibility(View.VISIBLE);
                                        Log.v("MainActivity", "No connection");
                                        net.setText("No connection");
                                        Toast toast = Toast.makeText(getApplicationContext(),
                                                "No internet connection.",
                                                Toast.LENGTH_SHORT);

                                        toast.show();
                                    }
                                }
                            }
                        }
                );
            } else {
                Toast.makeText(this, "Turn on location", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        } else {
            requestPermissions();
        }
    }

    @SuppressLint("MissingPermission")
    private void requestNewLocationData(){

        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(0);
        mLocationRequest.setFastestInterval(0);
        mLocationRequest.setNumUpdates(1);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mFusedLocationClient.requestLocationUpdates(
                mLocationRequest, mLocationCallback,
                Looper.myLooper()
        );

    }

    private LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            Location mLastLocation = locationResult.getLastLocation();
            //lat.setText("Latitude :"+mLastLocation.getLatitude()+"");
            //lon.setText("Longitude :"+mLastLocation.getLongitude()+"");
            //LAT = mLastLocation.getLatitude()+"";
            //LON = mLastLocation.getLongitude()+"";
        }
    };

    private boolean checkPermissions() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        return false;
    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions(
                this,
                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                PERMISSION_ID
        );
    }

    private boolean isLocationEnabled() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
                LocationManager.NETWORK_PROVIDER
        );
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_ID) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLastLocation();
            }
        }
    }

    @Override
    public void onResume(){
        super.onResume();
        if (checkPermissions()) {
            getLastLocation();
        }

    } */

    public static void updateURL(String lat, String lon) {
        request_url += "&lat="+lat+"&lon="+lon;
        Log.v("MainActivity", "New url: " + request_url);
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
                WeatherContract.WeatherEntry.COLUMN_SUMMARY };

        // Perform a query on the weather table
        Cursor cursor = db.query(
                WeatherContract.WeatherEntry.TABLE_NAME,   // The table to query
                projection,            // The columns to return
                null,                  // The columns for the WHERE clause
                null,                  // The values for the WHERE clause
                null,                  // Don't group the rows
                null,                  // Don't filter by row groups
                null);                   // The sort order

        TextView displayView = (TextView) findViewById(R.id.text_view);

        try {
            // Display the number of rows in the Cursor (which reflects the number of rows in the
            // pets table in the database).
            displayView.setText("Number of rows in weather database table: " + cursor.getCount()+"\n");

            // In the while loop below, iterate through the rows of the cursor and display
            // the information from each column in this order.
            displayView.append(WeatherContract.WeatherEntry._ID + " - " +
                    WeatherContract.WeatherEntry.COLUMN_DATE+ " - " +
                    WeatherContract.WeatherEntry.COLUMN_MAX_TEMP + " - " +
                    WeatherContract.WeatherEntry.COLUMN_MIN_TEMP+ " - " +
                    WeatherContract.WeatherEntry.COLUMN_SUMMARY + "\n");

            // Figure out the index of each column
            int idColumnIndex = cursor.getColumnIndex(WeatherContract.WeatherEntry._ID);
            int dateColumnIndex = cursor.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_DATE);
            int maxColumnIndex = cursor.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_MAX_TEMP);
            int minColumnIndex = cursor.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_MIN_TEMP);
            int summaryColumnIndex = cursor.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_SUMMARY);

            // Iterate through all the returned rows in the cursor
            while (cursor.moveToNext()) {
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
            }

        } finally {
            // Always close the cursor when you're done reading from it. This releases all its
            // resources and makes it invalid.
            cursor.close();
        }
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
            if(weather != null) {
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
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

}