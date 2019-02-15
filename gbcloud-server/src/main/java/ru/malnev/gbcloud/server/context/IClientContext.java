package ru.malnev.gbcloud.server.context;

import org.jetbrains.annotations.NotNull;
import ru.malnev.gbcloud.common.conversations.IConversationManager;
import ru.malnev.gbcloud.common.transport.ITransportChannel;

public interface IClientContext
{
    @NotNull
    ITransportChannel getTransportChannel();

    void setTransportChannel(@NotNull ITransportChannel transportChannel);

    @NotNull
    IConversationManager getConversationManager();

    void setConversationManager(@NotNull IConversationManager conversationManager);

    boolean isAuthenticated();

    void setAuthenticated(boolean value);
}
