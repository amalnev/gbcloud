package ru.malnev.gbcloud.client.transport;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import lombok.SneakyThrows;
import ru.malnev.gbcloud.client.config.ClientConfig;
import ru.malnev.gbcloud.client.events.EConnectionClosed;
import ru.malnev.gbcloud.common.transport.INetworkEndpoint;
import ru.malnev.gbcloud.common.transport.Netty;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.enterprise.inject.spi.CDI;
import javax.inject.Inject;

@Netty
@ApplicationScoped
public class NettyClient implements INetworkEndpoint
{
    private volatile ChannelFuture channelFuture;

    @Inject
    private ClientConfig config;

    @Inject
    private Event<EConnectionClosed> connectionClosedBus;

    private Thread workerThread;

    private final Runnable worker = new Runnable()
    {
        @Override
        public void run()
        {
            final EventLoopGroup group = new NioEventLoopGroup();
            try
            {
                final Bootstrap bootstrap = new Bootstrap();
                bootstrap.group(group)
                        .channel(NioSocketChannel.class)
                        .handler(new ChannelInitializer<SocketChannel>()
                        {
                            @Override
                            protected void initChannel(final SocketChannel socketChannel)
                            {
                                socketChannel.pipeline().addLast(new ObjectEncoder(),
                                        new ObjectDecoder(ClassResolvers.cacheDisabled(null)),
                                        CDI.current().select(NettyClientHandler.class).get());
                            }
                        });
                channelFuture = bootstrap.connect(config.getServerAddress(), config.getServerPort()).sync();
                channelFuture.channel().closeFuture().sync();
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }
            finally
            {
                group.shutdownGracefully();
                connectionClosedBus.fireAsync(new EConnectionClosed());
            }
        }
    };

    @Override
    @SneakyThrows
    public void start()
    {
        workerThread = new Thread(worker);
        workerThread.start();
    }

    @Override
    @SneakyThrows
    public void stop()
    {
        if (channelFuture != null) channelFuture.channel().close();
        if (workerThread != null) workerThread.join();
        channelFuture = null;
        workerThread = null;
    }
}
