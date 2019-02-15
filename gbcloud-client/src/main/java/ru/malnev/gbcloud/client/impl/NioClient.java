package ru.malnev.gbcloud.client.impl;

import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import ru.malnev.gbcloud.client.conversations.ClientConversationManager;
import ru.malnev.gbcloud.client.conversations.IClientAgent;
import ru.malnev.gbcloud.client.conversations.KeepAliveClientAgent;
import ru.malnev.gbcloud.client.events.EMessageReceived;
import ru.malnev.gbcloud.common.messages.IMessage;
import ru.malnev.gbcloud.common.transport.INetworkEndpoint;
import ru.malnev.gbcloud.common.transport.ITransportChannel;
import ru.malnev.gbcloud.common.transport.NioTransportChannel;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.enterprise.inject.spi.CDI;
import javax.inject.Inject;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;

@ApplicationScoped
public class NioClient implements INetworkEndpoint
{
    private static final int PING_PERIOD = 1000;

    @Inject
    @Getter
    @Setter
    private ClientConversationManager conversationManager;

    @Getter
    private final ITransportChannel transportChannel;

    @Inject
    private Event<EMessageReceived> messageReceivedBus;

    private final Selector selector;

    private final Thread pingWorker;

    @SneakyThrows
    public NioClient()
    {
        selector = Selector.open();
        final SocketChannel socketChannel = SocketChannel.open(new InetSocketAddress("localhost", 9999));
        socketChannel.configureBlocking(false);
        socketChannel.register(selector, SelectionKey.OP_READ);
        final NioTransportChannel channel = CDI.current().select(NioTransportChannel.class).get();
        channel.setSocketChannel(socketChannel);
        transportChannel = channel;

        pingWorker = new Thread(() ->
        {
            while(transportChannel.isConnected())
            {
                IClientAgent keepAliveAgent = CDI.current().select(KeepAliveClientAgent.class).get();
                keepAliveAgent.setTransportChannel(transportChannel);
                keepAliveAgent.setConversationManager(conversationManager);
                conversationManager.startConversation(keepAliveAgent);

                try
                {
                    Thread.sleep(PING_PERIOD);
                }
                catch (InterruptedException e)
                {
                    break;
                }
            }
        });
    }

    @Override
    @SneakyThrows
    public void start()
    {
        pingWorker.start();
        while (transportChannel.isConnected())
        {
            selector.select();
            if (!selector.isOpen()) break;
            for(final SelectionKey selectionKey : selector.selectedKeys())
            {
                if (selectionKey.isReadable())
                {
                    try
                    {
                        final IMessage message = transportChannel.readMessage();
                        messageReceivedBus.fireAsync(new EMessageReceived(conversationManager, message, transportChannel));
                    }
                    catch (final ITransportChannel.CorruptedDataReceived e)
                    { //ignore

                    }
                    catch (Exception e)
                    {
                        transportChannel.closeSilently();
                        break;
                    }
                }
            }
            selector.selectedKeys().clear();
        }
    }

    @Override
    @SneakyThrows
    public void stop()
    {
        pingWorker.interrupt();
        if (selector.isOpen()) selector.close();
        transportChannel.closeSilently();
    }
}
