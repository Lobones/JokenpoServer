package me.lobones.jokenpo.server;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import me.lobones.jokenpo.Main;

public class ServerNetworkHandler extends SimpleChannelInboundHandler<ByteBuf> {

    private Channel channel;

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        System.out.println("channel active - " + ctx.channel());

        if (Main.getGameManager().getOpponent() == null) {
            Main.getGameManager().setOpponent(ctx.channel());
            System.out.println("opponent set - " + ctx.channel());

            int symbol = Main.getGameManager().getPlayerData().getSymbol() == 'X' ? 1 : 0;
            ByteBuf callback;
            callback = Unpooled.buffer();
            callback.writeInt(4);
            callback.writeInt(symbol);
            ctx.channel().writeAndFlush(callback, ctx.voidPromise());
        } else {
            ByteBuf callback;
            callback = Unpooled.buffer();
            callback.writeInt(5);
            ctx.channel().writeAndFlush(callback, ctx.voidPromise());
            ctx.channel().close();
            System.out.println("game in progress, channel kicked - " + ctx.channel());
        }
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ByteBuf byteBuf) throws Exception {
        ByteBuf callback;

        switch (byteBuf.readInt()) {
            case 0:
                callback = Unpooled.buffer();
                callback.writeInt(0);
                callback.writeLong(byteBuf.readLong());
                getChannel().writeAndFlush(callback, getChannel().voidPromise());
                break;
            case 1:
                callback = Unpooled.buffer();
                callback.writeInt(1);
                callback.writeLong(System.currentTimeMillis() + (short) (Math.random() * Short.MAX_VALUE));
                getChannel().writeAndFlush(callback, getChannel().voidPromise());
                break;
            case 2:
                getChannel().close();
                break;
            case 3:
                int button = byteBuf.readInt();
                char actionSymbol = byteBuf.readInt() == 0 ? 'X' : 'O';
                System.out.println("server - received opponent action (button " + button + " = " + actionSymbol + ")");
                Main.getGameManager().getButtonMap().get(button).setSymbol(actionSymbol);
                Main.getGameManager().setTurn(Main.getGameManager().getPlayerData().getSymbol());
                break;
            default:
                break;
        }
    }

    public Channel getChannel() {
        return channel;
    }
}
