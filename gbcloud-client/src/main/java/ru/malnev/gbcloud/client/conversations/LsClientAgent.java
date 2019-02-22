package ru.malnev.gbcloud.client.conversations;

import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import ru.malnev.gbcloud.common.conversations.AbstractConversation;
import ru.malnev.gbcloud.common.conversations.ActiveAgent;
import ru.malnev.gbcloud.common.messages.IMessage;
import ru.malnev.gbcloud.common.messages.LsRequest;
import ru.malnev.gbcloud.common.messages.LsResponse;

@ActiveAgent
public class LsClientAgent extends AbstractConversation
{
    private static final int TIMEOUT_OVERRIDE = 60000; //1m

    @Getter
    @Setter
    private String requestedPath;



    @Override
    public void processMessageFromPeer(@NotNull IMessage message)
    {
        if (message instanceof LsResponse)
        {
            //lsConversationCompleteBus.fireAsync(new ELsConversationComplete(this, (LsResponse) message));
        }
    }

    @Override
    public void start()
    {
        setTimeoutMillis(TIMEOUT_OVERRIDE);
        expectMessage(LsResponse.class);
        final LsRequest outgoingMessage = new LsRequest(requestedPath);
        sendMessageToPeer(outgoingMessage);
    }
}
