package me.lobones.jokenpo.gui;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import me.lobones.jokenpo.Main;
import me.lobones.jokenpo.ui.JokenpoButton;

import javax.swing.*;
import java.awt.*;

public class GuiGameScreen extends JFrame {
    private final JPanel centerPanel;
    private final JLabel topLabel;
    private final JLabel bottomLabel;

    public GuiGameScreen(boolean isServer, String address, String version) {
        this.setTitle("Jokenpo -" + (isServer ? " Host" : " Client") + " (" + address + " - " + version + ")");
        this.setSize(500, 450);
        this.setLocationRelativeTo(null);

        JPanel panel = new JPanel();

        centerPanel = new JPanel(new GridLayout(3, 3));

        for (int i = 0; i < 9; i++) {
            JokenpoButton jokenpoButton = new JokenpoButton(i, "");
            jokenpoButton.addActionListener(e -> {
                if (Main.getGameManager().getTurn() != Main.getGameManager().getPlayerData().getSymbol()) {
                    showMessage("Wait your turn", "This is your opponent's turn, wait until it's your turn to play.");
                    return;
                }

                if (jokenpoButton.isEmpty()) {
                    if (isServer) {
                        ByteBuf byteBuf = Unpooled.buffer();
                        byteBuf.writeInt(3);
                        byteBuf.writeInt(jokenpoButton.getId());
                        byteBuf.writeInt(Main.getGameManager().getPlayerData().getSymbol() == 'X' ? 0 : 1);
                        Main.getGameManager().getOpponent().writeAndFlush(byteBuf);
                        jokenpoButton.setSymbol(Main.getGameManager().getPlayerData().getSymbol());
                        Main.getGameManager().setTurn(Main.getGameManager().getPlayerData().getSymbol() == 'X' ? 'O' : 'X');
                    } else {
                        ByteBuf byteBuf = Unpooled.buffer();
                        byteBuf.writeInt(3);
                        byteBuf.writeInt(jokenpoButton.getId());
                        byteBuf.writeInt(Main.getGameManager().getPlayerData().getSymbol() == 'X' ? 0 : 1);
                        Main.getGameManager().getClient().getChannel().writeAndFlush(byteBuf);
                        jokenpoButton.setSymbol(Main.getGameManager().getPlayerData().getSymbol());
                        Main.getGameManager().setTurn(Main.getGameManager().getPlayerData().getSymbol() == 'X' ? 'O' : 'X');
                    }
                }
            });

            centerPanel.add(jokenpoButton);
        }

        topLabel = new JLabel("TURN: X", SwingConstants.CENTER);
        bottomLabel = new JLabel("? - ? - ?", SwingConstants.CENTER);

        int margin = 20;
        panel.setLayout(new BorderLayout(margin, margin));
        panel.setBorder(BorderFactory.createEmptyBorder(margin, margin, margin, margin));
        panel.add(topLabel, BorderLayout.PAGE_START);
        panel.add(centerPanel, BorderLayout.CENTER);
        panel.add(bottomLabel, BorderLayout.PAGE_END);

        getContentPane().add(panel);

        if (isServer) {
            centerPanel.setVisible(false);
            topLabel.setText("Please, tell your opponent to connect to " + address);
            bottomLabel.setText("Waiting Opponent...");
        }

        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setVisible(true);
    }

    public void opponentConnected() {
        centerPanel.setVisible(true);
        topLabel.setText("YOUR ARE: " + Main.getGameManager().getPlayerData().getSymbol() + " | TURN: X");
        bottomLabel.setText("? - ? - ?");
    }

    public void showMessage(String title, String message) {
        JOptionPane.showMessageDialog(this, message, title, JOptionPane.PLAIN_MESSAGE);
    }

    public JPanel getCenterPanel() {
        return centerPanel;
    }

    public JLabel getTopLabel() {
        return topLabel;
    }

    public JLabel getBottomLabel() {
        return bottomLabel;
    }
}
