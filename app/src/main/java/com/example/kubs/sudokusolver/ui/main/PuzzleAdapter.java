package com.example.kubs.sudokusolver.ui.main;

import android.content.Context;
import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.kubs.sudokusolver.R;

import java.util.List;

public class PuzzleAdapter extends RecyclerView.Adapter<PuzzleAdapter.PuzzleViewHolder> {

    private final LayoutInflater mInflater;
    private List<Puzzle> mPuzzles;

    PuzzleAdapter(Context context) {
        mInflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public PuzzleViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = mInflater.inflate(R.layout.row_item, viewGroup, false);
        return new PuzzleViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull PuzzleViewHolder puzzleViewHolder, int i) {
        if (mPuzzles != null) {
            Puzzle current = mPuzzles.get(i);
            puzzleViewHolder.textView.setText(current.toString());
        } else {
            puzzleViewHolder.textView.setText(Resources.getSystem().getString(R.string.no_puzzle_string));
        }
    }

    void setPuzzles(List<Puzzle> puzzles) {
        mPuzzles = puzzles;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        if (mPuzzles != null) {
            return mPuzzles.size();
        }
        else {
            return 0;
        }
    }

    public class PuzzleViewHolder extends RecyclerView.ViewHolder {
        private final TextView textView;

        private PuzzleViewHolder(View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.puzzleTextView);
        }
    }
}
