package ru.malnev.gbcloud.server.conversations;

import org.jetbrains.annotations.Nullable;
import ru.malnev.gbcloud.common.conversations.AbstractConversation;
import ru.malnev.gbcloud.common.conversations.PassiveAgent;
import ru.malnev.gbcloud.server.filesystem.ServerDirectory;
import ru.malnev.gbcloud.server.persistence.entitites.User;

import java.nio.file.Path;

@PassiveAgent
public abstract class ServerAgent extends AbstractConversation
{
    @Nullable
    public User getUser()
    {
        final ServerConversationManager conversationManager = (ServerConversationManager) getConversationManager();
        return conversationManager.getUser();
    }

    public Path getCurrentDirectory()
    {
        final ServerConversationManager conversationManager = (ServerConversationManager) getConversationManager();
        return conversationManager.getServerDirectory().getCurrentDirectory();
    }

    public ServerDirectory getServerDirectory()
    {
        final ServerConversationManager conversationManager = (ServerConversationManager) getConversationManager();
        return conversationManager.getServerDirectory();
    }
}
