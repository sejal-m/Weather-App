package com.example.weather.data;

import android.provider.BaseColumns;

public final class WeatherContract {

    // To prevent someone from accidentally instantiating the contract class
    private WeatherContract() {}

    public static final class WeatherEntry implements BaseColumns {

        /** Name of database table for weather */
        public static final String TABLE_NAME = "weather";

        public static final String _ID = BaseColumns._ID;

        public static final String COLUMN_DATE ="date";

        public static final String COLUMN_MIN_TEMP = "min";

        public static final String COLUMN_MAX_TEMP = "max";

        public static final String COLUMN_TEMP = "temperature";

        public static final String COLUMN_HUMIDITY = "humidity";

        public static final String COLUMN_WIND_SPEED = "wind_speed";

        public static final String COLUMN_PRESSURE = "pressure";

        public static final String COLUMN_SUNRISE = "sunrise";

        public static final String COLUMN_SUNSET = "sunset";

        public static final String COLUMN_VISIBILITY = "visibility";

        public static final String COLUMN_DESCRIPTION = "description";

        public static final String COLUMN_WEATHER_CODE = "weather_code";

    }
}