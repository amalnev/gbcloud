package ru.malnev.gbcloud.server.transport;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import ru.malnev.gbcloud.common.messages.IMessage;
import ru.malnev.gbcloud.common.transport.ITransportChannel;
import ru.malnev.gbcloud.common.transport.Netty;
import ru.malnev.gbcloud.common.transport.NettyTransportChannel;
import ru.malnev.gbcloud.server.context.IClientContext;
import ru.malnev.gbcloud.server.conversations.ServerConversationManager;
import ru.malnev.gbcloud.server.events.EClientConntected;
import ru.malnev.gbcloud.server.events.EMessageReceived;

import javax.enterprise.event.Event;
import javax.inject.Inject;

public class NettyServerHandler extends ChannelInboundHandlerAdapter
{
    @Inject
    private IClientContext clientContext;

    @Inject
    private ServerConversationManager conversationManager;

    @Inject
    @Netty
    private ITransportChannel transportChannel;

    @Inject
    private Event<EClientConntected> clientConntectedBus;

    @Inject
    private Event<EMessageReceived> messageReceivedBus;

    @Override
    public void channelActive(final ChannelHandlerContext ctx)
    {
        clientContext.setConversationManager(conversationManager);
        ((NettyTransportChannel) transportChannel).setChannelContext(ctx);
        clientContext.getConversationManager().setTransportChannel(transportChannel);
        clientConntectedBus.fireAsync(new EClientConntected(clientContext));
    }

    @Override
    public void channelRead(final ChannelHandlerContext ctx,
                            final Object msg)
    {
        if (msg == null) return;
        if (!(msg instanceof IMessage)) return;
        messageReceivedBus.fireAsync(new EMessageReceived(clientContext, (IMessage) msg));
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
