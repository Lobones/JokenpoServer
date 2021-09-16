package me.lobones.jokenpo.gui;

import me.lobones.jokenpo.Main;

import javax.swing.*;

public class GuiMenuScreen extends JFrame {
    private final String version;

    public GuiMenuScreen(String version) {
        this.version = version;

        this.setTitle("Jokenpo (" + version + ")");
        this.setSize(500, 450);
        this.setLocationRelativeTo(null);

        setLayout();

        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setVisible(true);
    }

    public void showMessage(String title, String message) {
        JOptionPane.showMessageDialog(this, message, title, JOptionPane.PLAIN_MESSAGE);
    }

    private void setLayout() {
        JPanel panel = new JPanel();

        JLabel hostAddressLabel = new JLabel("IP:");
        hostAddressLabel.setBounds(20, 10, 80, 20);

        JLabel hostPortLabel = new JLabel("PORT:");
        hostPortLabel.setBounds(20, 40, 80, 20);

        JTextField hostAddress = new JTextField("127.0.0.1");
        hostAddress.setSize(100, 20);
        hostAddress.setBounds(70, 10, 100, 20);

        JTextField hostPort = new JTextField("8000");
        hostPort.setSize(100, 20);
        hostPort.setBounds(70, 40, 100, 20);

        JButton openServerBtn = new JButton("Open Server");
        openServerBtn.setBounds(50, 100, 100, 50);
        openServerBtn.addActionListener(e -> Main.getGameManager().startGameScreen(true, hostAddress.getText(), Integer.parseInt(hostPort.getText())));

        JButton joinServerBtn = new JButton("Join Server");
        joinServerBtn.setBounds(50, 100, 100, 50);
        joinServerBtn.addActionListener(e -> Main.getGameManager().startGameScreen(false, hostAddress.getText(), Integer.parseInt(hostPort.getText())));

        add(hostAddressLabel);
        add(hostAddress);

        add(hostPortLabel);
        add(hostPort);

        panel.add(openServerBtn);
        panel.add(joinServerBtn);

        this.getContentPane().add(panel);
    }

    public String getVersion() {
        return version;
    }
}
