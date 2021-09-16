package me.lobones.jokenpo;

import io.netty.channel.Channel;
import me.lobones.jokenpo.client.Client;
import me.lobones.jokenpo.data.PlayerData;
import me.lobones.jokenpo.gui.GuiGameScreen;
import me.lobones.jokenpo.gui.GuiMenuScreen;
import me.lobones.jokenpo.server.Server;
import me.lobones.jokenpo.ui.JokenpoButton;

import java.util.HashMap;
import java.util.Map;

public class GameManager {

    private GuiMenuScreen guiMenuScreen;
    private GuiGameScreen guiGameScreen;

    private Server server;
    private Client client;

    private char turn = 'X';

    private String address;
    private int port;

    private Channel opponent;

    private PlayerData playerData;

    private final Map<Integer, JokenpoButton> buttonMap = new HashMap<>();

    public GameManager() {
        startMainScreen();
    }

    public void startMainScreen() {
        this.guiMenuScreen = new GuiMenuScreen(Main.getVersion());
    }

    public void startGameScreen(boolean host, String address, int port) {
        if (host) {
            try {
                getGuiMenuScreen().setVisible(false);
                this.server = new Server(address, port);
                int random = (int) (Math.floor(Math.random() * 10) + 1);
                char symbol = random % 2 == 0 ? 'X' : 'O';
                System.out.println("server - random symbol defined " + symbol + " (code " + random + ")");
                setPlayerData(new PlayerData(symbol, 0, true)); // x = 0 | o = 1
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        } else {
            try {
                this.address = address;
                this.port = port;
                this.client = new Client(address, port);
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }
    }

    public void setPlayerData(PlayerData playerData) {
        this.playerData = playerData;
    }

    public PlayerData getPlayerData() {
        return playerData;
    }

    public GuiMenuScreen getGuiMenuScreen() {
        return guiMenuScreen;
    }

    public void setGuiGameScreen(GuiGameScreen guiGameScreen) {
        this.guiGameScreen = guiGameScreen;
    }

    public GuiGameScreen getGuiGameScreen() {
        return guiGameScreen;
    }

    public Client getClient() {
        return client;
    }

    public void setTurn(char turn) {
        this.turn = turn;
        getGuiGameScreen().getTopLabel().setText("YOU ARE: " + getPlayerData().getSymbol() + " | TURN: " + turn);
    }

    public char getTurn() {
        return turn;
    }

    public String getAddress() {
        return address;
    }

    public int getPort() {
        return port;
    }

    public void setOpponent(Channel opponent) {
        this.opponent = opponent;
        getGuiGameScreen().opponentConnected();
        setTurn('X');
    }

    public Channel getOpponent() {
        return opponent;
    }

    public Map<Integer, JokenpoButton> getButtonMap() {
        return buttonMap;
    }
}
