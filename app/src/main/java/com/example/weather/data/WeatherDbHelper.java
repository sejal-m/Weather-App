package com.example.weather.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.weather.WeatherData;

public class WeatherDbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "flurryData.db";

    public WeatherDbHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        String CREATE_WEATHER_TABLE = "CREATE TABLE " + WeatherContract.WeatherEntry.TABLE_NAME + "("
                + WeatherContract.WeatherEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + WeatherContract.WeatherEntry.COLUMN_CITY + " TEXT, "
                + WeatherContract.WeatherEntry.COLUMN_DATE + " TEXT, "
                + WeatherContract.WeatherEntry.COLUMN_MIN_TEMP + " REAL NOT NULL, "
                + WeatherContract.WeatherEntry.COLUMN_MAX_TEMP + " REAL NOT NULL, "
                + WeatherContract.WeatherEntry.COLUMN_TEMP + " REAL NOT NULL, "
                + WeatherContract.WeatherEntry.COLUMN_HUMIDITY + " REAL, "
                + WeatherContract.WeatherEntry.COLUMN_WIND_SPEED + " REAL, "
                + WeatherContract.WeatherEntry.COLUMN_PRESSURE + " REAL, "
                + WeatherContract.WeatherEntry.COLUMN_SUNRISE + " TEXT, "
                + WeatherContract.WeatherEntry.COLUMN_SUNSET + " TEXT, "
                + WeatherContract.WeatherEntry.COLUMN_VISIBILITY + " REAL, "
                + WeatherContract.WeatherEntry.COLUMN_DESCRIPTION + " TEXT, "
                + WeatherContract.WeatherEntry.COLUMN_WEATHER_CODE + " INT );";
        sqLiteDatabase.execSQL(CREATE_WEATHER_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + WeatherContract.WeatherEntry.TABLE_NAME);
    }

    public boolean addRecord(WeatherData weather) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(WeatherContract.WeatherEntry.COLUMN_CITY, weather.getCity());
        values.put(WeatherContract.WeatherEntry.COLUMN_DATE, weather.getDate());
        values.put(WeatherContract.WeatherEntry.COLUMN_MAX_TEMP, weather.getMax_temp());
        values.put(WeatherContract.WeatherEntry.COLUMN_MIN_TEMP, weather.getMin_temp());
        values.put(WeatherContract.WeatherEntry.COLUMN_TEMP, weather.getTemp());
        values.put(WeatherContract.WeatherEntry.COLUMN_WIND_SPEED, weather.getWind_speed());
        values.put(WeatherContract.WeatherEntry.COLUMN_HUMIDITY, weather.getHumidity());
        values.put(WeatherContract.WeatherEntry.COLUMN_PRESSURE, weather.getPressure());
        values.put(WeatherContract.WeatherEntry.COLUMN_SUNRISE, weather.getSunrise());
        values.put(WeatherContract.WeatherEntry.COLUMN_SUNSET, weather.getSunset());
        values.put(WeatherContract.WeatherEntry.COLUMN_VISIBILITY, weather.getVisibility());
        values.put(WeatherContract.WeatherEntry.COLUMN_DESCRIPTION, weather.getDescription());
        values.put(WeatherContract.WeatherEntry.COLUMN_WEATHER_CODE, weather.getWeather_code());

        long insert = db.insert(WeatherContract.WeatherEntry.TABLE_NAME, null, values);

        return insert != -1;
    }

    public WeatherData fetchLastEntry() {
        WeatherData weather = null;

        String queryString = "SELECT * FROM " + WeatherContract.WeatherEntry.TABLE_NAME
                + " WHERE rowid = ( SELECT MAX(rowid) FROM " + WeatherContract.WeatherEntry.TABLE_NAME + " )";
        Log.v("WeatherDbHelper", queryString);
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(queryString, null);

        if (c != null) {
            if (c.moveToFirst()) { // if Cursor is not empty
                int tempColumnIndex = c.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_TEMP);
                int minColumnIndex = c.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_MIN_TEMP);
                int maxColumnIndex = c.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_MAX_TEMP);
                int descColumnIndex = c.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_DESCRIPTION);
                int dateColumnIndex = c.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_DATE);
                int humidityColumnIndex = c.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_HUMIDITY);
                int pressureColumnIndex = c.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_PRESSURE);
                int sunriseColumnIndex = c.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_SUNRISE);
                int sunsetColumnIndex = c.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_SUNSET);
                int visibilityColumnIndex = c.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_VISIBILITY);
                int speedColumnIndex = c.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_WIND_SPEED);
                int codeColumnIndex = c.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_WEATHER_CODE);

                String fetchedTemp = String.format("%.5g%n", c.getDouble(tempColumnIndex));
                String fetchedMin = String.format("%.5g%n", c.getDouble(minColumnIndex));
                String fetchedMax = String.format("%.5g%n", c.getDouble(maxColumnIndex));
                String fetchedDesc = c.getString(descColumnIndex);
                String fetchedDate = c.getString(dateColumnIndex);
                String fetchedHumidity = String.valueOf(c.getDouble(humidityColumnIndex));
                String fetchedPressure = String.valueOf(c.getDouble(pressureColumnIndex));
                String fetchedVisibility = String.valueOf(c.getDouble(visibilityColumnIndex));
                String fetchedSpeed = String.valueOf(c.getDouble(speedColumnIndex));
                String fetchedSunrise = c.getString(sunriseColumnIndex);
                String fetchedSunset = c.getString(sunsetColumnIndex);
                int fetchedCode = c.getInt(codeColumnIndex);
                Log.v("WeatherDbHelper", fetchedTemp);

                weather = new WeatherData(fetchedDate, Double.parseDouble(fetchedMin), Double.parseDouble(fetchedMax), Double.parseDouble(fetchedTemp), Double.parseDouble(fetchedSpeed), Double.parseDouble(fetchedHumidity), Double.parseDouble(fetchedPressure), fetchedSunrise, fetchedSunset, Double.parseDouble(fetchedVisibility), fetchedCode, fetchedDesc);
            } else {
                Log.v("WeatherDbHelper", "cursor empty");
            }
        } else {
            Log.v("WeatherDbHelper", "cursor null");
        }

        c.close();
        db.close();
        return weather;
    }

}
