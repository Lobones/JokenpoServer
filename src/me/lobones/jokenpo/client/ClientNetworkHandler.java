package me.lobones.jokenpo.client;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import me.lobones.jokenpo.Main;
import me.lobones.jokenpo.data.PlayerData;
import me.lobones.jokenpo.gui.GuiGameScreen;

import java.net.ConnectException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class ClientNetworkHandler extends SimpleChannelInboundHandler<ByteBuf> {

    private static final SimpleDateFormat FORMAT = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        Main.getGameManager().getGuiMenuScreen().setVisible(false);
        Main.getGameManager().setGuiGameScreen(new GuiGameScreen(false, Main.getGameManager().getAddress() + ":" + Main.getGameManager().getPort(), Main.getVersion()));
        System.out.println("client - connected");
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ByteBuf byteBuf) throws Exception {
        switch (byteBuf.readInt()) {
            case 0:
                System.out.println("ping - " + TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - byteBuf.readLong()) + "ms");
                break;
            case 1:
                System.out.println("time offset - " + FORMAT.format(new Date(byteBuf.readLong() - System.currentTimeMillis())));
                break;
            case 3:
                int button = byteBuf.readInt();
                char actionSymbol = byteBuf.readInt() == 0 ? 'X' : 'O';
                System.out.println("client - received opponent action (button " + button + " = " + actionSymbol + ")");
                Main.getGameManager().getButtonMap().get(button).setSymbol(actionSymbol);
                Main.getGameManager().setTurn(Main.getGameManager().getPlayerData().getSymbol());
                break;
            case 4:
                int code = byteBuf.readInt();
                char symbol = code == 0 ? 'X' : 'O';
                System.out.println("client - received symbol " + symbol + " (code " + code + ")");
                Main.getGameManager().setPlayerData(new PlayerData(symbol, 0, false));
                Main.getGameManager().getGuiGameScreen().getTopLabel().setText("YOU ARE: " + symbol + " | TURN: X");
                break;
            case 5:
                System.out.println("client - kicked (5) [cause game in progress]");
                Main.getGameManager().getGuiMenuScreen().setVisible(true);
                Main.getGameManager().getGuiGameScreen().setVisible(false);
                Main.getGameManager().getGuiGameScreen().setEnabled(false);
                Main.getGameManager().getGuiMenuScreen().showMessage("Game in progress", "The game you tried to join is already in progress.");
                break;
            default:
                break;
        }
    }

}
