package com.example.kubs.sudokusolver;

import android.arch.persistence.room.TypeConverter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;


public class Converters {
    @TypeConverter
    public static Integer[][] fromString(String value){
        Type listType = new TypeToken<Integer[][]>(){}.getType();
        return new Gson().fromJson(value,listType);
    }

    @TypeConverter
    public static String fromIntegerArray(Integer[][] list){
        Gson gson = new Gson();
        String json = gson.toJson(list);
        return json;
    }
}
