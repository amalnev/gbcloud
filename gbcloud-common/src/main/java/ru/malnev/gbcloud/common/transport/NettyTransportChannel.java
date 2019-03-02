package ru.malnev.gbcloud.common.transport;

import io.netty.channel.ChannelHandlerContext;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import ru.malnev.gbcloud.common.logging.CommonLogger;
import ru.malnev.gbcloud.common.messages.IMessage;

import javax.interceptor.Interceptors;
import java.io.IOException;
import java.net.InetSocketAddress;

@Netty
@Interceptors(CommonLogger.class)
public class NettyTransportChannel implements ITransportChannel
{
    private static final int MTU = 1024 * 1024; //1M

    @Getter
    @Setter
    private ChannelHandlerContext channelContext;

    @Override
    public synchronized void sendMessage(@NotNull IMessage message)
    {
        channelContext.writeAndFlush(message);
    }

    @Override
    public @NotNull IMessage readMessage()
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public synchronized String getRemoteAddress()
    {
        return ((InetSocketAddress) channelContext.channel().remoteAddress()).getHostName();
    }

    @Override
    public synchronized boolean isConnected()
    {
        if (channelContext == null) return false;
        return channelContext.channel().isActive();
    }

    @Override
    public int getMTU()
    {
        return MTU;
    }

    @Override
    public synchronized void close() throws IOException
    {
        channelContext.close();
    }
}
