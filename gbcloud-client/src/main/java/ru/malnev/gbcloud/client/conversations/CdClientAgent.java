package ru.malnev.gbcloud.client.conversations;

import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import ru.malnev.gbcloud.common.conversations.AbstractConversation;
import ru.malnev.gbcloud.common.conversations.ActiveAgent;
import ru.malnev.gbcloud.common.messages.CdFailResponse;
import ru.malnev.gbcloud.common.messages.CdRequest;
import ru.malnev.gbcloud.common.messages.IMessage;
import ru.malnev.gbcloud.common.messages.ServerOkResponse;

import javax.inject.Inject;

@ActiveAgent
public class CdClientAgent extends AbstractConversation
{
    private static final String FAIL_MESSAGE = "Remote change dir failed. Reason: ";

    @Inject
    private CdRequest request;

    @Getter
    @Setter
    private String targetDirectory;

    @Override
    public void processMessageFromPeer(@NotNull IMessage message)
    {
        if(message instanceof CdFailResponse)
        {
            final CdFailResponse cdFailResponse = (CdFailResponse) message;
            System.out.println(FAIL_MESSAGE + cdFailResponse.getReason());
        }
        getConversationManager().stopConversation(this);
    }

    @Override
    public void start()
    {
        expectMessage(ServerOkResponse.class);
        expectMessage(CdFailResponse.class);
        request.setTargetDirectory(targetDirectory);
        sendMessageToPeer(request);
    }
}
