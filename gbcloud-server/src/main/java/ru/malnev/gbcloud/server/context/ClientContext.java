package ru.malnev.gbcloud.server.context;

import lombok.Getter;
import lombok.Setter;
import ru.malnev.gbcloud.common.conversations.IConversationManager;
import ru.malnev.gbcloud.common.transport.ITransportChannel;

@Getter
@Setter
public class ClientContext implements IClientContext
{
    private volatile ITransportChannel transportChannel;

    private volatile IConversationManager conversationManager;

    private volatile boolean authenticated = true;
}
