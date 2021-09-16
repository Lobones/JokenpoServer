package me.lobones.jokenpo;

public class Main {
    private static final long serialVersionUID = 1L;
    private static final String version = "alpha-0.0.1";

    private static GameManager gamaManager;

    public static void main(String[] args) {
        gamaManager = new GameManager();
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public static String getVersion() {
        return version;
    }

    public static GameManager getGameManager() {
        return gamaManager;
    }
}
