package ru.malnev.gbcloud.server.transport;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import ru.malnev.gbcloud.common.messages.IMessage;
import ru.malnev.gbcloud.common.transport.ITransportChannel;
import ru.malnev.gbcloud.common.transport.Netty;
import ru.malnev.gbcloud.common.transport.NettyTransportChannel;
import ru.malnev.gbcloud.server.conversations.ServerConversationManager;
import ru.malnev.gbcloud.server.events.EClientConnected;
import ru.malnev.gbcloud.server.events.EMessageReceived;

import javax.enterprise.event.Event;
import javax.inject.Inject;

public class NettyServerHandler extends ChannelInboundHandlerAdapter
{
    @Inject
    private ServerConversationManager conversationManager;

    @Inject
    @Netty
    private ITransportChannel transportChannel;

    @Inject
    private Event<EClientConnected> clientConntectedBus;

    @Inject
    private Event<EMessageReceived> messageReceivedBus;

    @Override
    public void channelActive(final ChannelHandlerContext ctx)
    {
        ((NettyTransportChannel) transportChannel).setChannelContext(ctx);
        conversationManager.setTransportChannel(transportChannel);
        clientConntectedBus.fireAsync(new EClientConnected(conversationManager));
    }

    @Override
    public void channelRead(final ChannelHandlerContext ctx,
                            final Object msg)
    {
        if (msg == null) return;
        if (!(msg instanceof IMessage)) return;
        messageReceivedBus.fireAsync(new EMessageReceived(conversationManager, (IMessage) msg));
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx)
    {
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
    {
        cause.printStackTrace();
        ctx.close();
    }
}
