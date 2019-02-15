package ru.malnev.gbcloud.client.conversations;

import org.jetbrains.annotations.NotNull;
import ru.malnev.gbcloud.common.conversations.IConversation;
import ru.malnev.gbcloud.common.transport.ITransportChannel;

public interface IClientAgent extends IConversation
{
    @NotNull ITransportChannel getTransportChannel();

    void setTransportChannel(@NotNull ITransportChannel transportChannel);

    void start();
}
