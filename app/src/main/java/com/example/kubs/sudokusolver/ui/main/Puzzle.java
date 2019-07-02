package com.example.kubs.sudokusolver.ui.main;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverters;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;

import java.util.Arrays;

@Entity(tableName = "puzzle_table")
public class Puzzle {

    @PrimaryKey(autoGenerate = true)
    private Long id;
    @NonNull
    @ColumnInfo(name = "puzzle")
    private Integer[][] values;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @NonNull
    public Integer[][] getValues() {
        return values;
    }

    public void setValues(@NonNull Integer[][] values) {
        this.values = values;
    }

    public Puzzle(@NonNull Integer[][] values) {

        this.values = values;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("---------------------------------\n");
        for (int i = 0; i < values.length; i++) {
            for (int j = 0; j < values[i].length; j++) {
                if(j==0){
                    sb.append("| ");
                }
                sb.append(values[i][j] + " ");
                if(j%3==2){
                    sb.append("| ");
                }
            }
            sb.append("\n");
            if(i%3==2){
                sb.append("---------------------------------\n");
            }
        }
        return sb.toString();
    }
}
