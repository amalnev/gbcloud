package ru.malnev.gbcloud.common.transport;

import org.jetbrains.annotations.NotNull;
import ru.malnev.gbcloud.common.messages.IMessage;

import java.io.Closeable;

public interface ITransportChannel extends Closeable
{
    void sendMessage(@NotNull IMessage message);

    @NotNull
    IMessage readMessage() throws CorruptedDataReceived, RemoteEndGone;

    default void closeSilently()
    {
        try
        {
            close();
        }
        catch (Exception e) {}
    }

    String getRemoteAddress();

    boolean isConnected();

    int getMTU();

    class CorruptedDataReceived extends Exception {}

    class RemoteEndGone extends Exception {}
}
