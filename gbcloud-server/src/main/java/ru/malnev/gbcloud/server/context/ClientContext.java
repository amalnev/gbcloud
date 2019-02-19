package ru.malnev.gbcloud.server.context;

import lombok.Getter;
import lombok.Setter;
import ru.malnev.gbcloud.common.conversations.IConversationManager;
import ru.malnev.gbcloud.common.transport.ITransportChannel;
import ru.malnev.gbcloud.server.conversations.ServerConversationManager;
import ru.malnev.gbcloud.server.persistence.entitites.User;

@Getter
@Setter
public class ClientContext implements IClientContext
{
    private volatile ITransportChannel transportChannel;

    private volatile ServerConversationManager conversationManager;

    @Override
    public boolean isAuthenticated()
    {
        return conversationManager.isAuthenticated();
    }
}
