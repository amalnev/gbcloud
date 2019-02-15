package ru.malnev.gbcloud.common.transport;

import lombok.*;
import org.jetbrains.annotations.NotNull;
import ru.malnev.gbcloud.common.logging.CommonLogger;
import ru.malnev.gbcloud.common.messages.IMessage;

import javax.interceptor.Interceptors;
import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.List;

@Interceptors(CommonLogger.class)
public class NioTransportChannel implements ITransportChannel
{
    private static final int READ_BUFFER_CAPACITY = 256;

    @Getter
    @Setter
    private SocketChannel socketChannel;

    @Override
    @SneakyThrows
    public synchronized void sendMessage(@NotNull IMessage message)
    {
        final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        final ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
        objectOutputStream.writeObject(message);
        objectOutputStream.close();
        final ByteBuffer sourceBuffer = ByteBuffer.wrap(byteArrayOutputStream.toByteArray());
        socketChannel.write(sourceBuffer);
    }

    @NotNull
    @Override
    @SneakyThrows
    public synchronized IMessage readMessage()
    {
        final ByteBuffer nioBuffer = ByteBuffer.allocate(READ_BUFFER_CAPACITY);
        final List<Byte> accumulator = new ArrayList<>();
        int read;
        while ((read = socketChannel.read(nioBuffer)) > 0)
        {
            nioBuffer.flip();
            final byte[] temporaryBuffer = new byte[nioBuffer.limit()];
            nioBuffer.get(temporaryBuffer);
            for (final byte b : temporaryBuffer) accumulator.add(b);
            nioBuffer.clear();
        }

        if (read < 0)
        {
            socketChannel.close();
            throw new RemoteEndGone();
        }

        final byte[] temporaryBuffer = new byte[accumulator.size()];
        int i = 0;
        for (final Byte b : accumulator) temporaryBuffer[i++] = b;
        final ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(temporaryBuffer);
        final ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream);

        try
        {
            final Object receivedObject = objectInputStream.readObject();
            if (receivedObject instanceof IMessage)
            {
                return (IMessage) receivedObject;
            }
        }
        catch (Exception e)
        {
            throw new CorruptedDataReceived();
        }

        throw new CorruptedDataReceived();
    }

    @Override
    public void closeSilently()
    {
        try
        {
            close();
        }
        catch (Exception e)
        {

        }
    }

    @Override
    public String getRemoteAddress()
    {
        if (!isConnected()) return null;
        return socketChannel.socket().getInetAddress().toString();
    }

    @Override
    public boolean isConnected()
    {
        if (socketChannel == null) return false;
        return socketChannel.isConnected();
    }

    @Override
    public void close() throws IOException
    {
        if (socketChannel != null)
            socketChannel.close();
    }
}
