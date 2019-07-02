package com.example.kubs.sudokusolver.ui.main;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import java.util.List;

public class PuzzleViewModel extends AndroidViewModel {

    private PuzzleRepository mRepository;
    private LiveData<List<Puzzle>> mAllPuzzles;

    public PuzzleViewModel(@NonNull Application application) {
        super(application);
        mRepository = new PuzzleRepository(application);
        mAllPuzzles = mRepository.getAllUsers();
    }

    LiveData<List<Puzzle>> getAllPuzzles() {
        return mAllPuzzles;
    }

    void insert(Puzzle puzzle){
        mRepository.insert(puzzle);
    }

    public void deleteAll() { mRepository.deleteAll(); }
}
