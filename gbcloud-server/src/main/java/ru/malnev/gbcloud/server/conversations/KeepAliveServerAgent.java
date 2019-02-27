package ru.malnev.gbcloud.server.conversations;

import org.jetbrains.annotations.NotNull;
import ru.malnev.gbcloud.common.conversations.RespondsTo;
import ru.malnev.gbcloud.common.messages.IMessage;
import ru.malnev.gbcloud.common.messages.ping.KeepAliveMessage;

import javax.inject.Inject;

@RespondsTo(KeepAliveMessage.class)
public class KeepAliveServerAgent extends ServerAgent
{
    @Inject
    private KeepAliveMessage response;

    @Override
    public synchronized void processMessageFromPeer(final @NotNull IMessage message)
    {
        sendMessageToPeer(response);
    }
}
