package ru.malnev.gbcloud.server.conversations;

import org.jetbrains.annotations.NotNull;
import ru.malnev.gbcloud.common.conversations.RespondsTo;
import ru.malnev.gbcloud.common.messages.IMessage;
import ru.malnev.gbcloud.common.messages.pwd.PwdRequest;
import ru.malnev.gbcloud.common.messages.pwd.PwdResponse;

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
