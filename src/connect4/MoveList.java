package connect4;

import java.util.ArrayList;
import java.util.Collections;

public class MoveList {
    public static int[] MOVE_ORDERING_SCORES = new int[Utils.WIDTH];
    ArrayList<Move> moves;

    MoveList() {
        moves = new ArrayList<>();
        for (int i = 0; i < Utils.WIDTH; i++) {
            MOVE_ORDERING_SCORES[i] = (Utils.WIDTH / 2 - Math.abs(i - Utils.WIDTH / 2)) * 10;
        }
    }

    public void add(int item) {
        moves.add(new Move(item, MOVE_ORDERING_SCORES[item]));
    }

    public void sort() {
        Collections.sort(moves);

    }

    public ArrayList<Move> getMoves() {
        return moves;
    }
}

class Move implements Comparable<Move> {
    private int move;
    private int score;

    Move(int move, int score) {
        this.move = move;
        this.score = score;
    }

    public int getMove() {
        return move;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    @Override
    // Note: We Want Best Score First, Hence Comparison Gets -Integer.compare (Gives Us Largest To Smallest)
    public int compareTo(Move b) {
        return -Integer.compare(this.score, b.score);
    }

    @Override
    public String toString() {
        return "" + this.move;
    }
}
