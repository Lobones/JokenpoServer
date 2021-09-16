package me.lobones.jokenpo.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import me.lobones.jokenpo.Main;
import me.lobones.jokenpo.gui.GuiGameScreen;

public class Server {
    public static final boolean EPOLL = Epoll.isAvailable();

    public Server(String host, int port) throws Exception {
        Main.getGameManager().setGuiGameScreen(new GuiGameScreen(true, host + ":" + port, Main.getVersion()));

        new Thread(() -> {
            EventLoopGroup eventLoopGroup = EPOLL ? new EpollEventLoopGroup() : new NioEventLoopGroup();

            try {
                new ServerBootstrap()
                        .group(eventLoopGroup)
                        .channel(EPOLL ? EpollServerSocketChannel.class : NioServerSocketChannel.class)
                        .childHandler(new ChannelInitializer<Channel>() {
                            @Override
                            protected void initChannel(Channel channel) throws Exception {
                                channel.pipeline().addLast(new ServerNetworkHandler());
                            }
                        }).bind(host, port).sync().channel().closeFuture().syncUninterruptibly();
            } catch (InterruptedException e) {
                Main.getGameManager().getGuiMenuScreen().showMessage("Error trying to open server", "An error occurred while trying to open the server, check IP and PORT");
                e.printStackTrace();
            } finally {
                eventLoopGroup.shutdownGracefully();
            }
        }).start();
    }

}
