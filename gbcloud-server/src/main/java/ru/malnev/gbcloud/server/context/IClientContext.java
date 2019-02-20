package ru.malnev.gbcloud.server.context;

import org.jetbrains.annotations.NotNull;
import ru.malnev.gbcloud.server.conversations.ServerConversationManager;

public interface IClientContext
{
    @NotNull
    ServerConversationManager getConversationManager();

    void setConversationManager(@NotNull ServerConversationManager conversationManager);

    boolean isAuthenticated();
}
