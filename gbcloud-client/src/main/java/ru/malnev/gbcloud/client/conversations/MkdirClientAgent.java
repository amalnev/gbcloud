package ru.malnev.gbcloud.client.conversations;

import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import ru.malnev.gbcloud.common.conversations.AbstractConversation;
import ru.malnev.gbcloud.common.conversations.ActiveAgent;
import ru.malnev.gbcloud.common.conversations.Expects;
import ru.malnev.gbcloud.common.conversations.StartsWith;
import ru.malnev.gbcloud.common.messages.IMessage;
import ru.malnev.gbcloud.common.messages.MkdirRequest;
import ru.malnev.gbcloud.common.messages.ServerOkResponse;

import javax.inject.Inject;

@ActiveAgent
@StartsWith(MkdirRequest.class)
@Expects(ServerOkResponse.class)
public class MkdirClientAgent extends AbstractConversation
{
    @Getter
    @Setter
    private String directoryPath;

    @Override
    protected void beforeStart(@NotNull IMessage initialMessage)
    {
        final MkdirRequest request = (MkdirRequest) initialMessage;
        request.setDirectoryPath(directoryPath);
    }

    @Override
    public void processMessageFromPeer(@NotNull IMessage message)
    {

    }
}
