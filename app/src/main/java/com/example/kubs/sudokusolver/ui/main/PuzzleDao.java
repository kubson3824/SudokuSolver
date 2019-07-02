package com.example.kubs.sudokusolver.ui.main;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface PuzzleDao {

    @Insert
    void insert(Puzzle puzzle);
    @Query("DELETE FROM puzzle_table")
    void deleteAll();
    @Query("Select * from puzzle_table")
    LiveData<List<Puzzle>> getAllPuzles();
}
