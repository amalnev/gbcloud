package ru.malnev.gbcloud.client.conversations;

import org.jetbrains.annotations.NotNull;
import ru.malnev.gbcloud.common.messages.IMessage;
import ru.malnev.gbcloud.common.messages.KeepAliveMessage;
import ru.malnev.gbcloud.common.transport.ITransportChannel;

public class KeepAliveClientAgent extends AbstractClientAgent
{
    private long startTime;

    @Override
    public void processMessage(final @NotNull IMessage message,
                               final @NotNull ITransportChannel transportChannel)
    {
        final long delta = System.currentTimeMillis() - startTime;
        System.out.println("Response from " +
                transportChannel.getRemoteAddress() +
                ". rtt=" +
                delta +
                "ms");
        getConversationManager().stopConversation(this);
    }

    @Override
    public void start()
    {
        startTime = System.currentTimeMillis();
        expectMessage(KeepAliveMessage.class);
        final IMessage outgoingMessage = new KeepAliveMessage();
        outgoingMessage.setConversationId(getId());
        getTransportChannel().sendMessage(outgoingMessage);
    }
}
