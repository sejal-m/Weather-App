package com.example.weather.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class WeatherDbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "flurry-database3.db";
    /**
     * Database version - have to implement if we change the schema.
     */
    private static final int DATABASE_VERSION = 1;

    public WeatherDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        //CREATE TABLE weather
        String SQL_CREATE_WEATHER_TABLE = "CREATE TABLE "+ WeatherContract.WeatherEntry.TABLE_NAME + "("
                + WeatherContract.WeatherEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + WeatherContract.WeatherEntry.COLUMN_DATE + " TEXT, "
                + WeatherContract.WeatherEntry.COLUMN_MIN_TEMP + " REAL NOT NULL, "
                + WeatherContract.WeatherEntry.COLUMN_MAX_TEMP + " REAL NOT NULL, "
                + WeatherContract.WeatherEntry.COLUMN_HUMIDITY + " REAL, "
                + WeatherContract.WeatherEntry.COLUMN_WIND_SPEED + " REAL, "
                + WeatherContract.WeatherEntry.COLUMN_WIND_DIRECTION + " REAL, "
                + WeatherContract.WeatherEntry.COLUMN_WIND_GUST + " REAL, "
                + WeatherContract.WeatherEntry.COLUMN_PRECIPITATION + " REAL, "
                + WeatherContract.WeatherEntry.COLUMN_PRECIPITATION_TYPE + " TEXT, "
                + WeatherContract.WeatherEntry.COLUMN_PRECIPITATION_PROBABILITY + " REAL, "
                + WeatherContract.WeatherEntry.COLUMN_SUNRISE + " TEXT, "
                + WeatherContract.WeatherEntry.COLUMN_SUNSET + " TEXT, "
                + WeatherContract.WeatherEntry.COLUMN_VISIBILITY + " REAL, "
                + WeatherContract.WeatherEntry.COLUMN_SUMMARY + " TEXT );";
        sqLiteDatabase.execSQL(SQL_CREATE_WEATHER_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
