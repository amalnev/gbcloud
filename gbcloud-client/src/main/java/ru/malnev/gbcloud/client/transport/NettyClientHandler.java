package ru.malnev.gbcloud.client.transport;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import ru.malnev.gbcloud.client.conversations.ClientConversationManager;
import ru.malnev.gbcloud.client.conversations.PingWorker;
import ru.malnev.gbcloud.client.events.EMessageReceived;
import ru.malnev.gbcloud.common.messages.IMessage;
import ru.malnev.gbcloud.common.transport.ITransportChannel;
import ru.malnev.gbcloud.common.transport.Netty;
import ru.malnev.gbcloud.common.transport.NettyTransportChannel;

import javax.enterprise.event.Event;
import javax.inject.Inject;

public class NettyClientHandler extends ChannelInboundHandlerAdapter
{
    @Inject
    private ClientConversationManager conversationManager;

    @Inject
    @Netty
    private ITransportChannel transportChannel;

    @Inject
    private Event<EMessageReceived> messageReceivedBus;

    @Inject
    private PingWorker pingWorker;

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception
    {
        ((NettyTransportChannel) transportChannel).setChannelContext(ctx);
        conversationManager.setTransportChannel(transportChannel);
        conversationManager.authenticate();
    }

    @Override
    public void channelRead(final ChannelHandlerContext ctx,
                            final Object msg)
    {
        if (msg == null) return;
        if (!(msg instanceof IMessage)) return;
        messageReceivedBus.fireAsync(new EMessageReceived((IMessage) msg));
    }

    @Override
    public void channelReadComplete(final ChannelHandlerContext ctx)
    {
        ctx.flush();
    }
}
