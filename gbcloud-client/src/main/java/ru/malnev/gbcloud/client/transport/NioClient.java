package ru.malnev.gbcloud.client.transport;

import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import ru.malnev.gbcloud.client.conversations.AuthenticationClientAgent;
import ru.malnev.gbcloud.client.conversations.ClientConversationManager;
import ru.malnev.gbcloud.client.conversations.PingWorker;
import ru.malnev.gbcloud.client.events.EMessageReceived;
import ru.malnev.gbcloud.common.messages.IMessage;
import ru.malnev.gbcloud.common.transport.INetworkEndpoint;
import ru.malnev.gbcloud.common.transport.ITransportChannel;
import ru.malnev.gbcloud.common.transport.Nio;
import ru.malnev.gbcloud.common.transport.NioTransportChannel;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.enterprise.inject.spi.CDI;
import javax.inject.Inject;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;

@Nio
@ApplicationScoped
public class NioClient implements INetworkEndpoint
{
    @Inject
    @Getter
    @Setter
    private ClientConversationManager conversationManager;

    @Nio
    @Inject
    @Getter
    private ITransportChannel transportChannel;

    @Inject
    private Event<EMessageReceived> messageReceivedBus;

    private final Selector selector;

    private final SocketChannel socketChannel;

    @SneakyThrows
    public NioClient()
    {
        selector = Selector.open();
        socketChannel = SocketChannel.open(new InetSocketAddress("localhost", 9999));
        socketChannel.configureBlocking(false);
        socketChannel.register(selector, SelectionKey.OP_READ);
    }

    @Override
    @SneakyThrows
    public void start()
    {
        ((NioTransportChannel) transportChannel).setSocketChannel(socketChannel);
        conversationManager.setTransportChannel(transportChannel);
        conversationManager.authenticate();

        while (transportChannel.isConnected())
        {
            selector.select();
            if (!selector.isOpen()) break;
            for (final SelectionKey selectionKey : selector.selectedKeys())
            {
                if (selectionKey.isReadable())
                {
                    try
                    {
                        final IMessage message = transportChannel.readMessage();
                        messageReceivedBus.fireAsync(new EMessageReceived(message));
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
        if (selector.isOpen()) selector.close();
        transportChannel.closeSilently();
    }
}
