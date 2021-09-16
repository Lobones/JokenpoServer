package me.lobones.jokenpo.ui;

import me.lobones.jokenpo.Main;

import javax.swing.*;

public class JokenpoButton extends JButton {

    private final int id;
    private char symbol;
    private boolean empty;

    public JokenpoButton(int id, String text) {
        super(text);
        this.id = id;
        this.empty = true;
        Main.getGameManager().getButtonMap().put(id, this);
    }

    public int getId() {
        return id;
    }

    public void setSymbol(char symbol) {
        this.symbol = symbol;
        setText(("" + symbol).toUpperCase());
        this.empty = false;
    }

    public boolean isEmpty() {
        return empty;
    }

    public char getSymbol() {
        return symbol;
    }
}
