package ru.malnev.gbcloud.server.conversations;

import org.jetbrains.annotations.Nullable;
import ru.malnev.gbcloud.common.conversations.AbstractConversation;
import ru.malnev.gbcloud.server.persistence.entitites.User;

public abstract class ServerAgent extends AbstractConversation
{
    @Nullable
    public User getUser()
    {
        final ServerConversationManager conversationManager = (ServerConversationManager) getConversationManager();
        return conversationManager.getUser();
    }
}
