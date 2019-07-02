package com.example.kubs.sudokusolver.ui.main;

import android.Manifest;
import android.annotation.SuppressLint;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kubs.sudokusolver.BoardAdapter;
import com.example.kubs.sudokusolver.MainActivity;
import com.example.kubs.sudokusolver.OnFragmentInteractionListener;
import com.example.kubs.sudokusolver.R;
import com.example.kubs.sudokusolver.SudokuFunctions;
import com.example.kubs.sudokusolver.sudoku.SudokuScanner;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import static android.app.Activity.RESULT_OK;

public class BoardFragment extends Fragment implements SensorEventListener {

    GridView gridView;
    Button solveButton;
    FloatingActionButton myCameraFab, myPhotosFab;
    Integer[][] board = populateNullBoard(new Integer[9][9]);
    BoardAdapter adapter;
    SensorManager sensorManager;
    Sensor accelerometer;
    long lastUpdate = 0;
    private PuzzleViewModel puzzleViewModel;
    private TextView progressText;

    float x = 0, y = 0, z = 0, last_x = 0, last_y = 0, last_z = 0;
    Uri file;
    private OnFragmentInteractionListener mListener;

    private static File getOutputMediaFile() {
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "SudokuSolver");

        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                return null;
            }
        }

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        return new File(mediaStorageDir.getPath() + File.separator +
                "IMG_" + timeStamp + ".jpg");
    }

    public Integer[][] populateNullBoard(Integer[][] board) {
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {
                if (board[i][j] == null)
                    board[i][j] = 0;
            }
        }
        return board;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.main_fragment, container, false);

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        mListener = (OnFragmentInteractionListener) getActivity();
        sensorManager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        gridView = getView().findViewById(R.id.sudokuGrid);
        puzzleViewModel = ViewModelProviders.of(this).get(PuzzleViewModel.class);
        progressText = getView().findViewById(R.id.progressText);

        adapter = new BoardAdapter(view.getContext(), board);
        gridView.setAdapter(adapter);
        solveButton = getView().findViewById(R.id.button);
        solveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new SolveAsyncTask().execute(board);

            }
        });
        myCameraFab = getView().findViewById(R.id.cameraButton);
        myCameraFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                file = Uri.fromFile(getOutputMediaFile());
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, file);
                startActivityForResult(cameraIntent, 100);

            }
        });
        myPhotosFab = getView().findViewById(R.id.galleryButton);
        myPhotosFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
                Intent getContentIntent = new Intent(Intent.ACTION_GET_CONTENT);
                getContentIntent.setType("image/*");
                getContentIntent.addCategory(Intent.CATEGORY_OPENABLE);
                Intent galleryIntent = Intent.createChooser(getContentIntent,getString(R.string.fileSelectionText));

                file = Uri.fromFile(getOutputMediaFile());
                if (galleryIntent.resolveActivity(getActivity().getPackageManager()) != null) {
                    startActivityForResult(Intent.createChooser(galleryIntent, getString(R.string.fileSelectionText)), 101);
                }
            }
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 100) {
            if (resultCode == RESULT_OK) {
                try {
                    Bitmap imageBitmap = BitmapFactory.decodeFile(file.getPath(), new BitmapFactory.Options());
                    new PhotoDetectionAsyncTask().execute(imageBitmap);
                    adapter = new BoardAdapter(getContext(), board);
                    gridView.setAdapter(adapter);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        if (requestCode == 101){
            if (resultCode == RESULT_OK) {
                try {
                    Bitmap imageBitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(),data.getData());
                    new PhotoDetectionAsyncTask().execute(imageBitmap);
                    adapter = new BoardAdapter(getContext(), board);
                    gridView.setAdapter(adapter);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private class SolveAsyncTask extends AsyncTask<Integer[][], Void, String>{

        Integer[][] temp;

        @Override
        protected void onPostExecute(String s) {
            progressText.setText(s);
            if (temp != null) {
                puzzleViewModel.insert(new Puzzle(board));
                mListener.onFragmentInteraction(temp,MainActivity.BOARD_FRAGMENT);
            } else {
                Toast.makeText(getContext(), getString(R.string.unsolvable_string), Toast.LENGTH_LONG).show();
            }

        }

        @Override
        protected void onPreExecute() {
            progressText.setText(getString(R.string.solvingText));
        }

        @Override
        protected String doInBackground(Integer[][]... integers) {
            try{
                temp = solve(integers[0]);
            } catch (Exception e){
                e.printStackTrace();
            }
            return getString(R.string.finishedSolvingText);
        }
    }
    private class PhotoDetectionAsyncTask extends AsyncTask<Bitmap, Void, String>{



        @Override
        protected void onPostExecute(String s) {
            progressText.setText(s);
            adapter = new BoardAdapter(getContext(), board);
            gridView.setAdapter(adapter);

        }

        @Override
        protected String doInBackground(Bitmap... bitmaps) {
            try {
                extractPuzzleFromImage(bitmaps[0]);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return getString(R.string.finished_loading);
        }

        @Override
        protected void onPreExecute() {
            progressText.setText(getString(R.string.loadingvalues_text));
        }
    }

    private void extractPuzzleFromImage(Bitmap imageBitmap) throws Exception {
        SudokuScanner scanner = new SudokuScanner(imageBitmap, this.getContext());
        scanner.getGrayscale();
        scanner.getThreshold();
        scanner.getLargestBlob();
        scanner.getHoughLines();
        scanner.getOutLine();
        scanner.extractPuzzle();
        board = scanner.getPuzzle();
        board = sanitizeBoard(board);
    }

    private Integer[][] sanitizeBoard(Integer[][] board) {
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {
                if (board[i][j] == null || board[i][j] <= 0) {
                    board[i][j] = 0;
                }
            }
        }
        return board;
    }

    private Integer[][] solve(Integer[][] board) {
        Integer[][] temp = new Integer[9][9];
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {
                temp[i][j] = board[i][j];
            }
        }
        for (int row = 0; row < 9; row++){
            for (int col = 0; col < 9; col++) {
                if (temp[row][col] == null) {
                    temp[row][col] = 0;
                }
            }
        }
        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 9; col++) {
               if (temp[row][col] == 0) {
                    for (int number = 1; number <= 9; number++) {
                        if (SudokuFunctions.isOk(temp, row, col, number)) {
                            temp[row][col] = number;
                            Integer[][] finalboard = solve(temp);
                            if (finalboard != null) {
                                return finalboard;
                            } else {
                                temp[row][col] = 0;
                            }
                        }
                    }

                    return null;
                }
            }
        }

        return temp;
    }

    @SuppressLint("RestrictedApi")
    @Override
    public void onSensorChanged(SensorEvent event) {
        Sensor mySensor = event.sensor;

        if (mySensor.getType() == Sensor.TYPE_ACCELEROMETER) {

            long curTime = System.currentTimeMillis();
            if ((curTime - lastUpdate) > 100) {
                long diffTime = (curTime - lastUpdate);
                lastUpdate = curTime;

                x = event.values[0];
                y = event.values[1];
                z = event.values[2];

                float speed = Math.abs(x + y + z - last_x - last_y - last_z) / diffTime * 10000;

                if (speed > 4000) {
                    Toast.makeText(getContext(), getString(R.string.clearedBoardText), Toast.LENGTH_SHORT).show();
                    for (int i = 0; i < board.length; i++) {
                        for (int j = 0; j < board[i].length; j++) {
                            board[i][j] = 0;
                            adapter.notifyDataSetChanged();
                            gridView.setAdapter(adapter);
                        }
                    }
                }
                last_x = x;
                last_y = y;
                last_z = z;
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
    }

    public void updatePuzzle(Integer[][] puzzle) {
        board = puzzle;
    }
}
