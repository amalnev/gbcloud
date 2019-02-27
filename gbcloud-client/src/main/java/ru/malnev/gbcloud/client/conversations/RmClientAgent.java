package ru.malnev.gbcloud.client.conversations;

import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import ru.malnev.gbcloud.common.conversations.AbstractConversation;
import ru.malnev.gbcloud.common.conversations.ActiveAgent;
import ru.malnev.gbcloud.common.conversations.Expects;
import ru.malnev.gbcloud.common.conversations.StartsWith;
import ru.malnev.gbcloud.common.messages.IMessage;
import ru.malnev.gbcloud.common.messages.ServerOkResponse;
import ru.malnev.gbcloud.common.messages.rm.RmRequest;

@ActiveAgent
@StartsWith(RmRequest.class)
@Expects(ServerOkResponse.class)
public class RmClientAgent extends AbstractConversation
{
    @Getter
    @Setter
    private String targetPath;

    @Override
    protected void beforeStart(@NotNull IMessage initialMessage)
    {
        final RmRequest request = (RmRequest) initialMessage;
        request.setTargetPath(targetPath);
    }

    @Override
    public void processMessageFromPeer(@NotNull IMessage message)
    {

    }
}
