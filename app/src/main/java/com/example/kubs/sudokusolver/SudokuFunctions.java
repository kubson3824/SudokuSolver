package com.example.kubs.sudokusolver;

public class SudokuFunctions {
    private static boolean isInRow(Integer[][] board, int row, int number){
        for (int i = 0; i <9 ; i++) {
            if(board[row][i] == number)
                return true;
        }
        return false;
    }
    private static boolean isInCol(Integer[][] board, int col, int number) {
        for (int i = 0; i < 9; i++)
            if (board[i][col] == number)
                return true;

        return false;
    }

    private static boolean isInBox(Integer[][] board, int row, int col, int number) {
        int r = row - row % 3;
        int c = col - col % 3;

        for (int i = r; i < r + 3; i++)
            for (int j = c; j < c + 3; j++)
                if (board[i][j] == number)
                    return true;

        return false;
    }

    public static boolean isOk(Integer[][] board, int row, int col, int number) {
        return !isInRow(board, row, number)  &&  !isInCol(board, col, number)  &&  !isInBox(board, row, col, number);
    }
}
