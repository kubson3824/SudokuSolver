package com.example.kubs.sudokusolver.ui.main;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.kubs.sudokusolver.MainActivity;
import com.example.kubs.sudokusolver.OnFragmentInteractionListener;
import com.example.kubs.sudokusolver.R;

import java.util.List;

public class PuzzleHistoryFragment extends Fragment {

    private PuzzleViewModel puzzleViewModel;

    private OnFragmentInteractionListener mListener;
    private FloatingActionButton fabDeletePuzzles;

    public static PuzzleHistoryFragment newInstance() {
        return new PuzzleHistoryFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.puzzle_history_fragment, container, false);
    }
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        mListener = (OnFragmentInteractionListener) getActivity();
        fabDeletePuzzles = getView().findViewById(R.id.deleteItemsFab);
        fabDeletePuzzles.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                puzzleViewModel.deleteAll();
                Toast.makeText(getContext(),getString(R.string.historyClearedText), Toast.LENGTH_SHORT).show();
            }
        });
        super.onViewCreated(view, savedInstanceState);
        final RecyclerView recyclerView = getView().findViewById(R.id.puzzleRecycler);
        final PuzzleAdapter adapter = new PuzzleAdapter(view.getContext());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        puzzleViewModel = ViewModelProviders.of(this).get(PuzzleViewModel.class);
        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getContext(), recyclerView, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Integer[][] downloaded = puzzleViewModel.getAllPuzzles().getValue().get(position).getValues();
                mListener.onFragmentInteraction(downloaded,MainActivity.USER_FRAGMENT);
                Toast.makeText(getContext(),getString(R.string.loadedHistoryValues),Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onLongItemClick(View view, int position) {

            }
        }){

        });
        puzzleViewModel.getAllPuzzles().observe(this, new Observer<List<Puzzle>>() {
            @Override
            public void onChanged(@Nullable List<Puzzle> puzzles) {
                adapter.setPuzzles(puzzles);
            }
        });
    }

}
