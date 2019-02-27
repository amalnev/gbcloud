package ru.malnev.gbcloud.server.transport;

import lombok.SneakyThrows;
import ru.malnev.gbcloud.common.messages.IMessage;
import ru.malnev.gbcloud.common.transport.*;
import ru.malnev.gbcloud.server.config.ServerConfig;
import ru.malnev.gbcloud.server.conversations.ServerConversationManager;
import ru.malnev.gbcloud.server.events.EClientConnected;
import ru.malnev.gbcloud.server.events.EClientDisconnected;
import ru.malnev.gbcloud.server.events.EMessageReceived;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.enterprise.inject.Default;
import javax.enterprise.inject.spi.CDI;
import javax.enterprise.util.AnnotationLiteral;
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
@Default
@ApplicationScoped
public class NioServer implements INetworkEndpoint
{
    private static final AnnotationLiteral<Nio> NIO_ANNOTATION = new AnnotationLiteral<Nio>() {};

    private Selector selector;

    private ServerSocketChannel socketChannel;

    @Inject
    private ServerConfig config;

    @Inject
    private Event<EClientConnected> clientConnectedBus;

    @Inject
    private Event<EMessageReceived> messageReceivedBus;

    @Inject
    private Event<EClientDisconnected> clientDisconnectedBus;

    @SneakyThrows
    public NioServer()
    {

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

        selector = Selector.open();
        socketChannel = ServerSocketChannel.open();
        socketChannel.bind(new InetSocketAddress("localhost", config.getServerPort()));
        socketChannel.configureBlocking(false);
        socketChannel.register(selector, SelectionKey.OP_ACCEPT);

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
                        final ServerConversationManager conversationManager = CDI.current().select(ServerConversationManager.class).get();
                        final NioTransportChannel transportChannel = CDI.current().select(NioTransportChannel.class, NIO_ANNOTATION).get();
                        transportChannel.setSocketChannel(clientSocketChannel);
                        conversationManager.setTransportChannel(transportChannel);
                        clientSocketChannel.register(selector, SelectionKey.OP_READ, conversationManager);
                        clientConnectedBus.fireAsync(new EClientConnected(conversationManager));
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                        closeSilently.accept(clientSocketChannel);
                    }
                }
                else if (selectionKey.isReadable())
                {
                    final ServerConversationManager conversationManager = (ServerConversationManager) selectionKey.attachment();
                    try
                    {
                        final IMessage message = conversationManager.getTransportChannel().readMessage();
                        messageReceivedBus.fireAsync(new EMessageReceived(conversationManager, message));
                    }
                    catch (final ITransportChannel.CorruptedDataReceived e)
                    { //ignore

                    }
                    catch (Exception e)
                    {
                        closeSilently.accept(conversationManager.getTransportChannel());
                        clientDisconnectedBus.fireAsync(new EClientDisconnected(conversationManager));
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
