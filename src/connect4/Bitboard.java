package connect4;

public class Bitboard {
    public long bitBoard;

    Bitboard() {
        bitBoard = 0L;
    }

    public void clearSquare(int square) {
        clearBit(square);
    }

    public void setSquare(int square) {
        setBit(square);
    }

    private void clearBit(int square) {
        bitBoard &= ~(1l << square);
    }

    private void setBit(int square) {
        bitBoard |= (1l << square);
    }

    public boolean getBit(int square) {
        return (bitBoard & (1L << square)) != 0;
    }

    public void setBitBoard(long bitBoard) {
        this.bitBoard = bitBoard;
    }
}
