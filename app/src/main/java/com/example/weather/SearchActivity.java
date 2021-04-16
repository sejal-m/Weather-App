package com.example.weather;

        import android.app.Dialog;
        import android.app.SearchManager;
        import android.content.Context;
        import android.content.Intent;
        import android.database.Cursor;
        import android.database.sqlite.SQLiteDatabase;
        import android.net.ConnectivityManager;
        import android.net.NetworkInfo;
        import android.os.AsyncTask;
        import android.os.Bundle;
        import android.util.Log;
        import android.view.View;
        import android.widget.Button;
        import android.widget.ImageView;
        import android.widget.LinearLayout;
        import android.widget.RelativeLayout;
        import android.widget.TextView;
        import android.widget.Toast;

        import androidx.annotation.NonNull;
        import androidx.appcompat.app.AppCompatActivity;
        import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

        import com.example.weather.data.WeatherContract;
        import com.example.weather.data.WeatherDbHelper;
        import com.google.android.material.bottomsheet.BottomSheetBehavior;

        import java.util.Calendar;

public class SearchActivity extends AppCompatActivity {

    Dialog myDialog;
    public static final String LOG_TAG = SearchActivity.class.getName();
    private static String initial_url = "https://api.openweathermap.org/data/2.5/weather?appid=fe21f6f759504260a7aa9a4c3b6a2492&q=";
    private static String request_url;
    SwipeRefreshLayout refreshLayout;
    TextView date_time, temp, min, max, desc, city;
    TextView details, humidity, sunrise, sunset, visibility, pressure, speed;
    LinearLayout detailed_forecast;
    RelativeLayout base_container, wrong;
    private BottomSheetBehavior mBottomSheetBehavior;
    ImageView weather_image;
    String city_name;
    Button search;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        Intent i = getIntent();
        city_name = i.getStringExtra("city_name");
        request_url = initial_url+city_name;
        Log.v("SearchActivity", request_url );
        base_container = findViewById(R.id.base_container);
        desc = findViewById(R.id.desc);
        min = findViewById(R.id.min_temp);
        max = findViewById(R.id.max_temp);
        date_time = findViewById(R.id.date_time);
        temp = findViewById(R.id.temp);
        city = findViewById(R.id.city_name);
        refreshLayout = findViewById(R.id.pullToRefresh);
        details = findViewById(R.id.details);
        detailed_forecast = findViewById(R.id.detailsContainer);
        humidity = findViewById(R.id.humidity);
        sunrise = findViewById(R.id.sunrise);
        sunset = findViewById(R.id.sunset);
        visibility = findViewById(R.id.visibility);
        pressure = findViewById(R.id.pressure);
        speed = findViewById(R.id.wind);
        weather_image = findViewById(R.id.weather_image);
        wrong = findViewById(R.id.wrong);
        search = findViewById(R.id.search_button);
        myDialog = new Dialog(this);

        View bottomSheet = findViewById(R.id.bottom_sheet);
        mBottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
        mBottomSheetBehavior.setHideable(false);

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_WEB_SEARCH);
                intent.putExtra(SearchManager.QUERY, city_name+" detailed forecast");
                startActivity(intent);
            }
        });

        mBottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                switch (newState) {
                    case BottomSheetBehavior.STATE_COLLAPSED:
                    case BottomSheetBehavior.STATE_SETTLING:
                        details.setVisibility(View.VISIBLE);
                        detailed_forecast.setVisibility(View.INVISIBLE);
                        //prec_probability.setText("50% chance of precipitation today.");
                        //prec_probability.setBackgroundColor(Color.parseColor("#4D1F9F"));
                        break;
                    case BottomSheetBehavior.STATE_DRAGGING: case BottomSheetBehavior.STATE_EXPANDED:
                        details.setVisibility(View.GONE);
                        detailed_forecast.setVisibility(View.VISIBLE);
                        //prec_probability.setText("Weather Details");
                        //prec_probability.setBackgroundColor(Color.parseColor("#ffffff"));
                        break;
                }
            }
            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                //sample.setText("Sliding...");
            }
        });

        updateBackground();


        if (isNetworkAvailable()) {
            new WeatherAsyncTask().execute(request_url);

        } else {
            Log.v("MainActivity", "No connection");
            ShowPopup();
            displayDatabaseInfo();
        }

        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (isNetworkAvailable()) {
                    new WeatherAsyncTask().execute(request_url);
                } else {
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

        if(timeOfDay >= 8 && timeOfDay < 22){
            base_container.setBackgroundResource(R.drawable.day_wallpaper);
        }else
            base_container.setBackgroundResource(R.drawable.night_wallpaper);
    }

    public void insertData(WeatherData weather) {

        WeatherDbHelper databaseHelper = new WeatherDbHelper(this);
        boolean b = databaseHelper.addRecord( weather );
        Log.v(LOG_TAG, "Table insertion: "+b);
        databaseHelper.fetchLastEntry();
    }

    public void displayDatabaseInfo() {
        WeatherDbHelper databaseHelper = new WeatherDbHelper(this);
        WeatherData weather = databaseHelper.fetchLastEntry();
        humidity.setText(String.valueOf(weather.getHumidity()));
        visibility.setText(String.valueOf(weather.getVisibility()));
        pressure.setText(String.valueOf(weather.getPressure()));
        speed.setText(String.valueOf(weather.getWind_speed()));
        sunrise.setText(weather.getSunrise());
        sunset.setText(weather.getSunset());
        min.setText("Min "+Math.round(weather.getMin_temp())+"°C");
        max.setText("Max "+Math.round(weather.getMax_temp())+"°C");
        desc.setText(capitalizeString(weather.getDescription()));
        temp.setText(String.valueOf(Math.round(weather.getTemp())));
        date_time.setText(weather.getDate());
        city.setText(capitalizeString(city_name));
        updateIcon(weather.getWeather_code());
        //updated.setText("Last Updated at "+QueryUtils.updatedAt+".");
    }

    private void updateIcon(int weather_code) {
        if(weather_code >= 200 && weather_code < 300)
            weather_image.setImageResource(R.drawable.thunderstorm);
        else if(weather_code >= 300 && weather_code < 500)
            weather_image.setImageResource(R.drawable.rain);
        else if(weather_code >= 500 && weather_code < 600)
            weather_image.setImageResource(R.drawable.rain);
        else if(weather_code >= 600 && weather_code < 700)
            weather_image.setImageResource(R.drawable.frost);
        else if(weather_code >= 700 && weather_code < 800)
            weather_image.setImageResource(R.drawable.haze);
        else if(weather_code == 800)
            weather_image.setImageResource(R.drawable.sun);
        else if(weather_code > 800)
            weather_image.setImageResource(R.drawable.cloud_with_sun);

    }

    private String capitalizeString(String s){
        String captilizedString="";
        if(!s.trim().equals("")){
            captilizedString = s.substring(0,1).toUpperCase() + s.substring(1);
        }
        return captilizedString;
    }
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private class WeatherAsyncTask extends AsyncTask<String, Void, WeatherData> {
        WeatherData result;
        @Override
        protected WeatherData doInBackground(String... urls) {

            if (urls.length < 1 || urls[0] == null) {
                return null;
            }

            result = QueryUtils.fetchWeatherData(urls[0]);
            Log.v("MainActivity", "Successful connection"+result);
            return result;
        }

        @Override
        protected void onPostExecute(WeatherData weather) {
            if (weather != null) {
                insertData(weather);

                Log.v(LOG_TAG, "Results: "+result.getSunrise()+" "+result.getSunset()+" "+result.getHumidity()+" "+result.getPressure()+" "+result.getVisibility()+" "+result.getWeather_code()+" "+
                result.getWind_speed());
                displayDatabaseInfo();

            }
            else {
                wrong.setVisibility(View.VISIBLE);
                base_container.setVisibility(View.GONE);
                details.setVisibility(View.GONE);
                detailed_forecast.setVisibility(View.GONE);
            }
        }


    }

}