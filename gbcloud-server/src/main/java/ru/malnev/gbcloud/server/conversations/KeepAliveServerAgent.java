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
    public void processMessage(final @NotNull IMessage message,
                               final @NotNull ITransportChannel transportChannel)
    {
        if (message instanceof KeepAliveMessage)
        {
            System.out.println("Ping request received from " + transportChannel.getRemoteAddress());

            final IMessage outgoingMessage = new KeepAliveMessage();
            outgoingMessage.setConversationId(getId());
            transportChannel.sendMessage(outgoingMessage);
            getConversationManager().stopConversation(this);
        }
    }
}
