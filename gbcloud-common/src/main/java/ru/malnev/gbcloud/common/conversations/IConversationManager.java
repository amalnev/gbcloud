package ru.malnev.gbcloud.common.conversations;

import org.jetbrains.annotations.NotNull;
import ru.malnev.gbcloud.common.messages.IMessage;
import ru.malnev.gbcloud.common.transport.ITransportChannel;

public interface IConversationManager
{
    void dispatchMessage(@NotNull IMessage message, @NotNull ITransportChannel transportChannel);

    void stopConversation(@NotNull IConversation conversation);
}
