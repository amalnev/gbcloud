package ru.malnev.gbcloud.server.conversations;

import org.jetbrains.annotations.NotNull;
import ru.malnev.gbcloud.common.conversations.RespondsTo;
import ru.malnev.gbcloud.common.events.EConversationComplete;
import ru.malnev.gbcloud.common.messages.IMessage;
import ru.malnev.gbcloud.common.messages.PwdRequest;
import ru.malnev.gbcloud.common.messages.PwdResponse;

import javax.enterprise.event.Event;
import javax.inject.Inject;

@RespondsTo(PwdRequest.class)
public class PwdServerAgent extends ServerAgent
{
    @Inject
    private PwdResponse response;

    @Override
    public void processMessageFromPeer(@NotNull IMessage message)
    {
        response.setCurrentDirectory(getCurrentDirectory().pwd());
        sendMessageToPeer(response);
    }
}
