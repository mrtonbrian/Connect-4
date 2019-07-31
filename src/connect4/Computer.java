package connect4;

import java.util.HashMap;
import java.util.Stack;

public class Computer extends Player {
    private static final int MAX_DEPTH = 42;
    private static final int TIME_PER_MOVE = 500;
    private static final int[] dx = {+1, +0, +1, -1};
    private static final int[] dy = {+0, +1, +1, +1};
    private HashMap<Long, Integer> table;
    private BBBoard board;
    private int nodesAB;
    private Stack<Integer> lastMoves = new Stack<>();
    private boolean searchStopped = false;
    private long startTime;

    Computer(int playerNum, BBBoard board) {
        super("Computer", playerNum);
        this.board = board;
    }

    private MoveList validSpots() {
        MoveList out = new MoveList();
        for (int i = 0; i < Utils.WIDTH; i++) {
            if (board.canPlace(i)) {
                out.add(i);
            }
        }

        return out;
    }

    private void makeMove(int move) {
        lastMoves.add(move);
        board.addToColumn(move);
    }

    private void takeMove() {
        board.removeFromBoard(lastMoves.pop());
    }


    private void checkUp() {
        long now = System.currentTimeMillis();
        searchStopped = (now - startTime >= TIME_PER_MOVE);
    }

    private int miniMaxAB(int depth, int alpha, int beta, boolean maximizingPlayer) {
        nodesAB++;
        if (depth == 0 || board.checkWinner() != Utils.NO_RESULT) {
            return heuristicScore();
        }

        if (nodesAB % 2048 == 0) {
            checkUp();
        }

        if (searchStopped) {
            return 0;
        }

        if (table.containsKey(board.hash())) {
            int value = table.get(board.hash());
            if (beta > value) {
                if (alpha >= beta) {
                    return beta;
                }
            }
        }

        if (maximizingPlayer) {
            int v = -1000000000;
            MoveList moveList = validSpots();

            moveList.sort();
            for (Move move : moveList.getMoves()) {
                makeMove(move.getMove());
                v = Math.max(v, miniMaxAB(depth - 1, alpha, beta, false));
                takeMove();

                alpha = Math.max(alpha, v);

                if (beta <= alpha) {
                    break;
                }
            }
            table.put(board.hash(), v - (-Utils.TERMINAL_SCORE) + 1);
            return v;
        } else {
            int v = +1000000000;
            MoveList moveList = validSpots();

            moveList.sort();
            for (Move move : moveList.getMoves()) {
                makeMove(move.getMove());
                v = Math.min(v, miniMaxAB(depth - 1, alpha, beta, true));
                takeMove();

                beta = Math.min(beta, v);

                if (beta <= alpha) {
                    break;
                }
            }
            table.put(board.hash(), v - (-Utils.TERMINAL_SCORE) + 1);
            return v;
        }
    }

    private int heuristicScore() {
        int result = board.checkWinner();
        if (result == getPlayerNum()) {
            return Utils.TERMINAL_SCORE;
        } else if (isToWin(getPlayerNum())) {
            return Utils.TERMINAL_SCORE / 10;
        } else if (result == (getPlayerNum() + 1) % 2) {
            return -Utils.TERMINAL_SCORE;
        } else if (isToWin((getPlayerNum() + 1) % 2)) {
            return -Utils.TERMINAL_SCORE / 10;
        } else if (result == Utils.TIE) {
            return 0;
        }

        int numCompFours = findNumWaysToWin(getPlayerNum());
        int numPlayFours = findNumWaysToWin((getPlayerNum() + 1) % 2);
        int numCompThree = findNumThrees(getPlayerNum());
        int numPlayThree = findNumThrees((getPlayerNum() + 1) % 2);

        return 40 * (numCompFours - numPlayFours) + 10 * (numCompThree - numPlayThree);
    }

    private int findNumWaysToWin(int color) {
        int numWays = 0;
        for (int y = 0; y < Utils.HEIGHT - 1; y++) {
            for (int x = 0; x < Utils.WIDTH; x++) {
                if (board.getPlayer(y, x) == Utils.NO_RESULT) {
                    makeMove(x);
                    numWays += numN(x, y, color, 4);
                    takeMove();
                }
            }
        }
        return numWays;
    }

    private int findNumThrees(int color) {
        int numWays = 0;
        for (int y = 0; y < Utils.HEIGHT - 1; y++) {
            for (int x = 0; x < Utils.WIDTH; x++) {
                if (board.getPlayer(y, x) == Utils.NO_RESULT) {
                    makeMove(x);
                    numWays += numN(x, y, color, 3);
                    takeMove();
                }
            }
        }
        return numWays;
    }

    private int numN(int x, int y, int color, int N) {
        int count = 0;
        for (int dir = 0; dir < 4; dir++) {
            if (makesN(x, y, color, dir, N)) {
                count++;
            }
        }

        return count;
    }

    private boolean isValid(int x, int y) {
        return (x >= 0 && x < Utils.WIDTH && y >= 0 && y < Utils.HEIGHT - 1);
    }

    private boolean isColor(int x, int y, int color) {
        if (color == Utils.PLAYERA) {
            return (board.bitboardA.getBit(Utils.rowColToSquare(y, x)));
        } else {
            return (board.bitboardB.getBit(Utils.rowColToSquare(y, x)));
        }
    }

    private boolean makesN(int x, int y, int color, int dir, int N) {
        int count = 0;
        int x1 = x;
        int y1 = y;
        while (isValid(x1, y1) && isColor(x1, y1, color)) {
            x1 += dx[dir];
            y1 += dy[dir];
            count++;
        }
        x1 = x;
        y1 = y;
        while (isValid(x1, y1) && isColor(x1, y1, color)) {
            x1 -= dx[dir];
            y1 -= dy[dir];
            count++;
        }
        return count - 1 >= N;
    }

    private boolean isToWin(int player) {
        if (board.checkWinner() == player) {
            return true;
        } else if (board.checkWinner() != Utils.NO_RESULT) {
            return false;
        }

        MoveList moveList = validSpots();
        moveList.sort();
        for (Move move : moveList.getMoves()) {
            makeMove(move.getMove());
            int result = board.checkWinner();
            takeMove();

            if (result == player) {
                return true;
            }
        }

        return false;
    }

    public int searchPosition() {
        table = new HashMap<>();
        startTime = System.currentTimeMillis();
        searchStopped = false;

        int bestScore = -Integer.MAX_VALUE;
        int bestMove = -1;

        MoveList moveList = validSpots();

        // Iterative Deepening
        int depth;
        for (depth = 1; depth < MAX_DEPTH; depth++) {
            nodesAB = 0;
            int tempBestScore = -Integer.MAX_VALUE;
            int tempBestMove = -1;

            moveList.sort();

            for (Move move : moveList.getMoves()) {
                makeMove(move.getMove());
                int moveScore = miniMaxAB(depth, -1000000000, 1000000000, false);
                takeMove();
                // Moves Likely To Be In Same Goodness-Order
                // Essentially A Very Simple History Heuristic
                move.setScore(move.getScore() + moveScore);
                if (searchStopped) {
                    System.out.printf("Best Score: %d Depth: %d Nodes Searched: %d NPS: %.1f\n",
                            bestScore, depth, nodesAB, nodesAB / (TIME_PER_MOVE / 1000.));
                    return bestMove;
                }
                if (moveScore > tempBestScore) {
                    tempBestScore = moveScore;
                    tempBestMove = move.getMove();
                }
            }

            bestScore = tempBestScore;
            bestMove = tempBestMove;
        }
        System.out.println(bestScore);
        return bestMove;
    }
}
