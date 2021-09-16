package me.lobones.jokenpo.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import me.lobones.jokenpo.Main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ConnectException;

public class Client {
    public static final boolean EPOLL = Epoll.isAvailable();

    private Channel channel;

    public Client(String host, int port) throws Exception {
        new Thread(() -> {
            EventLoopGroup eventLoopGroup = EPOLL ? new EpollEventLoopGroup() : new NioEventLoopGroup();

            try {
                channel = new Bootstrap()
                        .group(eventLoopGroup)
                        .channel(EPOLL ? EpollSocketChannel.class : NioSocketChannel.class)
                        .handler(new ChannelInitializer<Channel>() {
                            @Override
                            protected void initChannel(Channel channel) throws Exception {
                                channel.pipeline().addLast(new ClientNetworkHandler());
                            }
                        }).connect(host, port).sync().channel();

                BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
                String line;

                while ((line = reader.readLine()) != null) {
                    if ((line = line.toLowerCase()).length() == 0)
                        continue;

                    ByteBuf byteBuf = Unpooled.buffer();

                    if (line.startsWith("ping")) {
                        byteBuf.writeInt(0);
                        byteBuf.writeLong(System.nanoTime());
                    } else if (line.startsWith("time"))
                        byteBuf.writeInt(1);
                    else if (line.startsWith("exit")) {
                        byteBuf.writeInt(2);
                        channel.writeAndFlush(byteBuf, channel.voidPromise());
                        channel.closeFuture().syncUninterruptibly();
                        break;
                    }

                    channel.writeAndFlush(byteBuf, channel.voidPromise());
                }
            } catch (ConnectException e) {
                Main.getGameManager().getGuiMenuScreen().showMessage("Connection refused", "Connection refused: no further information: /127.0.0.1:8000");
                e.printStackTrace();
            } catch (InterruptedException | IOException e) {
                e.printStackTrace();
            } finally {
                eventLoopGroup.shutdownGracefully();
            }
        }).start();
    }

    public Channel getChannel() {
        return channel;
    }
}
