package me.lobones.jokenpo.data;

public class PlayerData {

    private char symbol;
    private int victories;

    private final boolean side;

    public PlayerData(char symbol, int victories, boolean side) {
        this.symbol = symbol;
        this.victories = victories;
        this.side = side;
    }

    public char getSymbol() {
        return symbol;
    }

    public void setSymbol(char symbol) {
        this.symbol = symbol;
    }

    public int getVictories() {
        return victories;
    }

    public void setVictories(int victories) {
        this.victories = victories;
    }

    public boolean isServerSide() {
        return side;
    }
}
