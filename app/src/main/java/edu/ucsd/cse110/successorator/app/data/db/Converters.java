package edu.ucsd.cse110.successorator.app.data.db;

import android.annotation.SuppressLint;

import androidx.room.TypeConverter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Converters {
    @SuppressLint("SimpleDateFormat")
    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    @TypeConverter
    public static Date fromString(String value) {
        try {
            return value == null ? null : sdf.parse(value);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    @TypeConverter
    public static String dateToString(Date date) {
        return date == null ? null : sdf.format(date);
    }
}
