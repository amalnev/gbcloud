package ru.malnev.gbcloud.server.conversations;

import lombok.Getter;
import lombok.Setter;
import ru.malnev.gbcloud.common.conversations.AbstractConversationManager;
import ru.malnev.gbcloud.server.filesystem.ServerDirectory;
import ru.malnev.gbcloud.server.persistence.entitites.User;

public class ServerConversationManager extends AbstractConversationManager
{
    @Getter
    @Setter
    private User user;

    public boolean isAuthenticated()
    {
        return user != null;
    }

    @Getter
    private ServerDirectory currentDirectory = new ServerDirectory();
}
