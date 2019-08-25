package connect4;

public class BBBoard {
    private static final long BOTTOM_ROW = (1L) | (1L << 7) | (1L << 14) | (1L << 21) | (1L << 28) | (1L << 35) | (1L << 42);
    public Bitboard bitboardA = new Bitboard();
    public Bitboard bitboardB = new Bitboard();
    private int turn = Utils.PLAYERA;
    private int[] heights = new int[Utils.WIDTH];

    private static boolean checkBitBoard(Bitboard b) {
        // Checks Vertical
        long tempBoard = b.bitBoard & (b.bitBoard >> 1);
        if ((tempBoard & (tempBoard >> 2 * 1)) != 0) {
            return true;
        }

        // Checks Horizontal
        tempBoard = b.bitBoard & (b.bitBoard >> Utils.HEIGHT);
        if ((tempBoard & (tempBoard >> 2 * Utils.HEIGHT)) != 0) {
            return true;
        }

        // Checks Down Diagonal
        tempBoard = b.bitBoard & (b.bitBoard >> Utils.HEIGHT - 1);
        if ((tempBoard & (tempBoard >> (2 * (Utils.HEIGHT - 1)))) != 0) {
            return true;
        }

        // Checks Up Diagonal
        tempBoard = b.bitBoard & (b.bitBoard >> Utils.HEIGHT + 1);
        return (tempBoard & (tempBoard >> (2 * (Utils.HEIGHT + 1)))) != 0;
    }

    public void resetBoard() {
        turn = randomizeTurn();
        bitboardA = new Bitboard();
        bitboardB = new Bitboard();
        heights = new int[Utils.WIDTH];
    }

    private int randomizeTurn() {
        return Math.random() < 0.5 ? Utils.PLAYERA : Utils.PLAYERB;
    }

    private int columnToSquare(int column) {
        return heights[column] + Utils.HEIGHT * column;
    }

    public int getHeight(int column) {
        return heights[column];
    }

    private void addToBoard(int square) {
        if (turn == Utils.PLAYERA) {
            bitboardA.setSquare(square);
            heights[Utils.squareToColumn(square)]++;
        } else {
            bitboardB.setSquare(square);
            heights[Utils.squareToColumn(square)]++;
        }
    }

    public void removeFromBoard(int column) {
        turn = (turn + 1) % 2;
        bitboardA.clearSquare(columnToSquare(column) - 1);
        bitboardB.clearSquare(columnToSquare(column) - 1);
        heights[column]--;
    }

    public boolean canPlace(int column) {
        return heights[column] < Utils.HEIGHT - 1;
    }

    public void addToColumn(int column) throws IllegalArgumentException {
        if (canPlace(column)) {
            addToBoard(columnToSquare(column));
            turn = (turn + 1) % 2;
        } else {
            throw new IllegalArgumentException("Column Full");
        }
    }

    public int checkWinner() {
        if (checkBitBoard(bitboardA)) {
            return Utils.PLAYERA;
        } else if (checkBitBoard(bitboardB)) {
            return Utils.PLAYERB;
        }

        for (int i = 0; i < Utils.WIDTH; i++) {
            if (heights[i] != Utils.HEIGHT - 1) {
                return Utils.NO_RESULT;
            }
        }

        return Utils.TIE;
    }

    public int getTurn() {
        return turn;
    }

    public void nullMove() {
        turn = (turn + 1) % 2;
    }

    public int getPlayer(int row, int col) {
        if (bitboardA.getBit(Utils.rowColToSquare(row, col))) {
            return Utils.PLAYERA;
        } else if (bitboardB.getBit(Utils.rowColToSquare(row, col))) {
            return Utils.PLAYERB;
        } else {
            return Utils.NO_RESULT;
        }
    }

    public long hash() {
        return (((bitboardA.bitBoard | bitboardB.bitBoard) << 1) | BOTTOM_ROW) ^ bitboardB.bitBoard;
    }
}
