package com.example.kubs.sudokusolver.sudoku;

public class SudokuStep {
    private int row, col, val;

    public SudokuStep(int row, int col, int val){
        this.row = row;
        this.col = col;
        this.val = val;
    }

    public int getRow() {
        return row;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public int getCol() {
        return col;
    }

    public void setCol(int col) {
        this.col = col;
    }

    public int getVal() {
        return val;
    }

    public void setVal(int val) {
        this.val = val;
    }
}
