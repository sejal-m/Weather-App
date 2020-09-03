package com.example.weather;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
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

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class MainActivity extends AppCompatActivity {
    private static boolean appended = false;
    FusedLocationProviderClient mFusedLocationClient;
    SwipeRefreshLayout refreshLayout;
    int PERMISSION_ID = 44;

    TextView desc, min, max, lat, lon, net, sample, timestamp;
    LinearLayout weather_data_container;
    protected LocationManager locationManager;
    String LAT = "sample", LON = "sample";
    protected Context context;
    TextView txtLat;
    String todays_date;
    String provider;
    protected String latitude, longitude;
    protected boolean gps_enabled, network_enabled;

    public static final String LOG_TAG = MainActivity.class.getName();

    private static String request_url = "https://api.openweathermap.org/data/2.5/weather?appid=fe21f6f759504260a7aa9a4c3b6a2492";

    private static final String FILE_NAME = "weatherData.txt";

    WeatherData weatherObj;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        desc = findViewById(R.id.desc);
        min = findViewById(R.id.min);
        max = findViewById(R.id.max);
        lat = findViewById(R.id.lat);
        lon = findViewById(R.id.lon);
        timestamp = findViewById(R.id.today);
        refreshLayout = findViewById(R.id.pullToRefresh);
        net = findViewById(R.id.check_network);
        weather_data_container = findViewById(R.id.weather_data_container);

        Log.v("MainActivity", "activity created1");

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        todays_date = QueryUtils.getCurrentDate();
        weatherObj = ReadObjectFromFile(FILE_NAME);
        //if(weatherObj == null || compareDates(todays_date,weatherObj.getTimestamp()) == false)
        if(compareDates(todays_date,weatherObj.getTimestamp()) == false) {
            Log.v(LOG_TAG, "On comparing, found false");
            getLastLocation();
        }

        else {
            displayWeatherData(weatherObj);
            Log.v(LOG_TAG, "On comparing, found true");
            Toast.makeText(this, "Displaying from file", Toast.LENGTH_LONG).show();
        }

        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if(compareDates(todays_date,weatherObj.getTimestamp()) == false) {
                    Log.v(LOG_TAG, "On comparing, found false");
                    getLastLocation();
                }
                else {
                    Log.v(LOG_TAG, "On comparing, found true");
                    displayWeatherData(weatherObj);
                    Toast.makeText(MainActivity.this, "Displaying from file", Toast.LENGTH_LONG).show();
                }
                refreshLayout.setRefreshing(false);
            }
        });


    }

    private void displayWeatherData(WeatherData weather) {
        desc.setText(weather.getDescription());
        min.setText("Min: "+weather.getMinTemp());
        max.setText("Max: "+weather.getMaxTemp());
        lat.setText("Latitude: "+weather.getLat());
        lon.setText("Longitude: "+weather.getLon());
        timestamp.setText("Date: "+weather.getTimestamp());
    }

    private boolean compareDates(String todays_date, String date_from_file) {
        return todays_date.equals(date_from_file);
    }

    @SuppressLint("MissingPermission")
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
                                    //lat.setText("Latitude :"+location.getLatitude()+"");
                                    //lon.setText("Longitude :"+location.getLongitude()+"");
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

    }

    public static void updateURL(String lat, String lon) {
        if(appended)
            return;
        request_url += "&lat="+lat+"&lon="+lon;
        Log.v("MainActivity", "New url: " + request_url);
        appended  = true;
    }

    private class EarthquakeAsyncTask extends AsyncTask<String, Void, WeatherData> {

        @Override
        protected WeatherData doInBackground(String... urls) {

            if (urls.length < 1 || urls[0] == null) {
                return null;
            }

            WeatherData result = QueryUtils.fetchWeatherData(urls[0]);
            WriteObjectToFile(result);
            return result;
        }

        @Override
        protected void onPostExecute(WeatherData weather) {
            weather = ReadObjectFromFile(FILE_NAME);
            Toast toast = null;
            if(weather != null) {
                //weather_data_container.setVisibility(View.VISIBLE);
                //net.setVisibility(View.GONE);
                displayWeatherData(weather);
                toast.makeText(MainActivity.this , "Displaying refreshed data", Toast.LENGTH_LONG).show();
            }
        }
    }
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
    public void WriteObjectToFile(Object serObj) {
        FileOutputStream fileOut = null;
        try {
            fileOut = openFileOutput(FILE_NAME, MODE_PRIVATE);
            ObjectOutputStream objectOut = new ObjectOutputStream(fileOut);
            objectOut.writeObject(serObj);
            objectOut.close();
            Toast.makeText(this, "Saved to " + getFilesDir() + "/" + FILE_NAME,
                    Toast.LENGTH_LONG).show();
            Log.v("MainActivity", "Saving Object");

        } catch (Exception ex) {
            ex.printStackTrace();
        }finally {
            if (fileOut != null) {
                try {
                    fileOut.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public WeatherData ReadObjectFromFile(String FILE_NAME) {
        FileInputStream fis = null;
        WeatherData weatherData = null;
        try {
            fis = openFileInput(FILE_NAME);
            ObjectInputStream ois = new ObjectInputStream(fis);
            weatherData = (WeatherData) ois.readObject();
            Log.v("Weather Data is- ", "Description- "+weatherData.getDescription()+" Temp- "+weatherData.getMaxTemp()+" Epoch- "+weatherData.getTimestamp());
            ois.close();
        }catch (Exception ex) {
            ex.printStackTrace();
        }
        return weatherData;
    }
}