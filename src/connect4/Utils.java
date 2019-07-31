package connect4;

import java.util.ArrayList;

public class Utils {
    public static final int PLAYERA = 0;
    public static final int PLAYERB = 1;
    public static final int TIE = 2;
    public static final int NO_RESULT = 3;

    // Note That Board Has 1 Padded Row (i.e. Board's Usable Height = Height - 1)
    public static final int HEIGHT = 7;
    public static final int WIDTH = 7;

    public static final int TERMINAL_SCORE = 10000;

    public static int rowColToSquare(int row, int col) {
        return col * HEIGHT + row;
    }

    public static int[] mirrorSquares(int row, int col) {
        return new int[]{Math.abs(row - Utils.HEIGHT), col};
    }

    public static int squareToColumn(int square) {
        return square / HEIGHT;
    }

    public static void printArrayListMoves(ArrayList<Move> list) {
        System.out.print("{");
        for (Move m : list) {
            System.out.print(m.getMove());

            if (m != list.get(list.size() - 1)) {
                System.out.print(", ");
            }
        }
        System.out.println("}");
    }
}
