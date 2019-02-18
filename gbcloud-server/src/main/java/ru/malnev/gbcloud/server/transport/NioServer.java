package ru.malnev.gbcloud.server.transport;

import lombok.SneakyThrows;
import ru.malnev.gbcloud.common.conversations.IConversationManager;
import ru.malnev.gbcloud.common.messages.IMessage;
import ru.malnev.gbcloud.common.transport.*;
import ru.malnev.gbcloud.server.context.IClientContext;
import ru.malnev.gbcloud.server.events.EClientConntected;
import ru.malnev.gbcloud.server.events.EClientDisconnected;
import ru.malnev.gbcloud.server.events.EMessageReceived;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.enterprise.inject.spi.CDI;
import javax.inject.Inject;
import java.io.Closeable;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.function.Consumer;

@Nio
@ApplicationScoped
public class NioServer implements INetworkEndpoint
{
    private Selector selector;

    private ServerSocketChannel socketChannel;

    @Inject
    private Event<EClientConntected> clientConntectedBus;

    @Inject
    private Event<EMessageReceived> messageReceivedBus;

    @Inject
    private Event<EClientDisconnected> clientDisconnectedBus;

    @SneakyThrows
    public NioServer()
    {
        selector = Selector.open();
        socketChannel = ServerSocketChannel.open();
        socketChannel.bind(new InetSocketAddress("localhost", 9999));
        socketChannel.configureBlocking(false);
        socketChannel.register(selector, SelectionKey.OP_ACCEPT);
    }

    @Override
    @SneakyThrows
    public void start()
    {
        final Consumer<Closeable> closeSilently = channel ->
        {
            try
            {
                if (channel != null)
                    channel.close();
            }
            catch (IOException e) {}
        };

        while (socketChannel.isOpen())
        {
            selector.select();
            if (!selector.isOpen()) break;
            for (final SelectionKey selectionKey : selector.selectedKeys())
            {
                if (selectionKey.isAcceptable())
                {
                    SocketChannel clientSocketChannel = null;
                    try
                    {
                        clientSocketChannel = ((ServerSocketChannel) selectionKey.channel()).accept();
                        clientSocketChannel.configureBlocking(false);
                        final IClientContext clientContext = CDI.current().select(IClientContext.class).get();
                        clientContext.setConversationManager(CDI.current().select(IConversationManager.class).get());
                        final NioTransportChannel transportChannel = CDI.current().select(NioTransportChannel.class, new NioLiteral()).get();
                        transportChannel.setSocketChannel(clientSocketChannel);
                        clientContext.setTransportChannel(transportChannel);
                        clientSocketChannel.register(selector, SelectionKey.OP_READ, clientContext);
                        clientConntectedBus.fireAsync(new EClientConntected(clientContext));
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                        closeSilently.accept(clientSocketChannel);
                    }
                }
                else if (selectionKey.isReadable())
                {
                    final IClientContext clientContext = (IClientContext) selectionKey.attachment();
                    try
                    {
                        final IMessage message = clientContext.getTransportChannel().readMessage();
                        messageReceivedBus.fireAsync(new EMessageReceived(clientContext, message));
                    }
                    catch (final ITransportChannel.CorruptedDataReceived e)
                    { //ignore

                    }
                    catch (Exception e)
                    {
                        closeSilently.accept(clientContext.getTransportChannel());
                        clientDisconnectedBus.fireAsync(new EClientDisconnected(clientContext));
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
        if (socketChannel.isOpen()) socketChannel.close();
    }
}
