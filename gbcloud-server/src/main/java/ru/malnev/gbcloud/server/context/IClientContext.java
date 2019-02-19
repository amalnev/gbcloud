package ru.malnev.gbcloud.server.context;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.malnev.gbcloud.common.conversations.IConversationManager;
import ru.malnev.gbcloud.common.transport.ITransportChannel;
import ru.malnev.gbcloud.server.conversations.ServerConversationManager;
import ru.malnev.gbcloud.server.persistence.entitites.User;

public interface IClientContext
{
    @NotNull
    ServerConversationManager getConversationManager();

    void setConversationManager(@NotNull ServerConversationManager conversationManager);

    boolean isAuthenticated();
}
