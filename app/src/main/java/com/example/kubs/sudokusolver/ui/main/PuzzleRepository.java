package com.example.kubs.sudokusolver.ui.main;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;

import java.util.List;

public class PuzzleRepository {
    private PuzzleDao mPuzzleDao;
    private LiveData<List<Puzzle>> mAllPuzzles;

    PuzzleRepository(Application application){
        PuzzleDatabase db = PuzzleDatabase.getDatabase(application);
        mPuzzleDao = db.puzzleDao();
        mAllPuzzles = mPuzzleDao.getAllPuzles();
    }

    LiveData<List<Puzzle>> getAllUsers(){
        return mAllPuzzles;
    }

    public void deleteAll() {
        new deleteAllAsyncTask(mPuzzleDao).execute();
    }

    private static class deleteAllAsyncTask extends AsyncTask<Puzzle, Void, Void> {

        private PuzzleDao mAsyncTaskDao;

        deleteAllAsyncTask(PuzzleDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final Puzzle... params) {
            mAsyncTaskDao.deleteAll();
            return null;
        }
    }


    public void insert (Puzzle puzzle){
        new insertAsyncTask(mPuzzleDao).execute(puzzle);
    }

    private static class insertAsyncTask extends AsyncTask<Puzzle, Void, Void> {

        private PuzzleDao mAsyncTaskDao;

        public insertAsyncTask(PuzzleDao mPuzzleDao) {
            mAsyncTaskDao = mPuzzleDao;
        }

        @Override
        protected Void doInBackground(Puzzle... puzzles) {
            mAsyncTaskDao.insert(puzzles[0]);
            return null;
        }
    }
}
