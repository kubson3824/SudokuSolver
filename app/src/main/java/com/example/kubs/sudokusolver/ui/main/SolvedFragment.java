package com.example.kubs.sudokusolver.ui.main;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import com.example.kubs.sudokusolver.R;
import com.example.kubs.sudokusolver.SolutionAdapter;

public class SolvedFragment extends Fragment {

    private GridView gridView;
    private SolutionAdapter adapter;
    private Integer[][] board;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.solved_fragment, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        gridView = getView().findViewById(R.id.solvedSudokuGrid);

        adapter = new SolutionAdapter(view.getContext(),board);
        gridView.setAdapter(adapter);
    }

    public void updatePuzzle(Integer[][] puzzle) {
        board = puzzle;
        adapter = new SolutionAdapter(getContext(),board);
        gridView.setAdapter(adapter);
    }
}
