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
        pingWorker.setTransportChannel(transportChannel);
        pingWorker.setConversationManager(conversationManager);
        pingWorker.start();
    }

    @Override
    public void channelRead(final ChannelHandlerContext ctx,
                            final Object msg)
    {
        if (msg == null) return;
        if (!(msg instanceof IMessage)) return;
        messageReceivedBus.fireAsync(new EMessageReceived(
                conversationManager,
                (IMessage) msg,
                transportChannel));
    }

    @Override
    public void channelReadComplete(final ChannelHandlerContext ctx)
    {
        ctx.flush();
    }
}
