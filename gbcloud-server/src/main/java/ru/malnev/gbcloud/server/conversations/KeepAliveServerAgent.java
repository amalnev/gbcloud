package ru.malnev.gbcloud.server.conversations;

import org.jetbrains.annotations.NotNull;
import ru.malnev.gbcloud.common.conversations.AbstractConversation;
import ru.malnev.gbcloud.common.messages.IMessage;
import ru.malnev.gbcloud.common.messages.KeepAliveMessage;
import ru.malnev.gbcloud.common.transport.ITransportChannel;

public class KeepAliveServerAgent extends AbstractConversation
{
    public KeepAliveServerAgent()
    {
        expectMessage(KeepAliveMessage.class);
    }

    @Override
    public synchronized void processMessageFromPeer(final @NotNull IMessage message)
    {
        if (message instanceof KeepAliveMessage)
        {
            sendMessageToPeer(new KeepAliveMessage());
            getConversationManager().stopConversation(this);
        }
    }
}
