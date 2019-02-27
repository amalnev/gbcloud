package ru.malnev.gbcloud.client.conversations;

import org.jetbrains.annotations.NotNull;
import ru.malnev.gbcloud.common.conversations.AbstractConversation;
import ru.malnev.gbcloud.common.conversations.ActiveAgent;
import ru.malnev.gbcloud.common.conversations.Expects;
import ru.malnev.gbcloud.common.conversations.StartsWith;
import ru.malnev.gbcloud.common.messages.IMessage;
import ru.malnev.gbcloud.common.messages.ping.KeepAliveMessage;

@ActiveAgent
@StartsWith(KeepAliveMessage.class)
@Expects(KeepAliveMessage.class)
public class KeepAliveClientAgent extends AbstractConversation
{
    private long startTime;

    @Override
    public synchronized void processMessageFromPeer(final @NotNull IMessage message)
    {
        final long delta = System.currentTimeMillis() - startTime;
        System.out.println("Response from " +
                getConversationManager().getTransportChannel().getRemoteAddress() +
                ". rtt=" +
                delta +
                "ms");
    }

    @Override
    protected void beforeStart(@NotNull IMessage initialMessage)
    {
        startTime = System.currentTimeMillis();
    }
}
