package ru.malnev.gbcloud.common.conversations;

import org.jetbrains.annotations.NotNull;
import ru.malnev.gbcloud.common.messages.IMessage;
import ru.malnev.gbcloud.common.transport.ITransportChannel;

public interface IConversationManager
{
    @NotNull
    ITransportChannel getTransportChannel();

    void setTransportChannel(@NotNull ITransportChannel transportChannel);

    void dispatchMessage(@NotNull IMessage message);

    void stopConversation(@NotNull IConversation conversation);

    void startConversation(@NotNull IConversation conversation);
}
