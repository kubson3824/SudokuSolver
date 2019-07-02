package com.example.kubs.sudokusolver.ui.main;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;
import android.content.Context;

import com.example.kubs.sudokusolver.Converters;

@Database(entities = {Puzzle.class}, version = 1, exportSchema = false)
@TypeConverters({Converters.class})
public abstract class PuzzleDatabase extends RoomDatabase {
    public abstract PuzzleDao puzzleDao();

    private static volatile PuzzleDatabase INSTANCE;

    static PuzzleDatabase getDatabase(final Context context){
        if (INSTANCE == null){
            synchronized (PuzzleDatabase.class) {
                if (INSTANCE == null){
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(), PuzzleDatabase.class, "puzzle_database").build();
                }
            }
        }
        return INSTANCE;
    }
}
