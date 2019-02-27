package ru.malnev.gbcloud.server.conversations;

import org.jetbrains.annotations.NotNull;
import ru.malnev.gbcloud.common.conversations.Expects;
import ru.malnev.gbcloud.common.conversations.FileTransferSendingAgent;
import ru.malnev.gbcloud.common.conversations.RespondsTo;
import ru.malnev.gbcloud.common.events.EFileTransferFailed;
import ru.malnev.gbcloud.common.messages.*;
import ru.malnev.gbcloud.common.messages.transfer.FileDataAcceptedResponse;
import ru.malnev.gbcloud.common.messages.transfer.FileTransferError;
import ru.malnev.gbcloud.common.messages.transfer.GetRequest;

import javax.enterprise.event.Event;
import javax.inject.Inject;
import java.io.IOException;
import java.nio.file.Path;

@RespondsTo(GetRequest.class)
@Expects({FileTransferError.class, FileDataAcceptedResponse.class})
public class GetServerAgent extends ServerAgent
{
    @Inject
    private FileTransferSendingAgent sendingAgent;

    @Inject
    private Event<EFileTransferFailed> fileTransferFailedBus;

    @Override
    protected synchronized void beforeFinish()
    {
        sendingAgent.stop();
    }

    @Override
    public void processMessageFromPeer(@NotNull IMessage message)
    {
        if (message instanceof GetRequest)
        {
            final GetRequest request = (GetRequest) message;
            final Path filePath = getCurrentDirectory().resolve(request.getFileName());
            try
            {
                sendingAgent.start(filePath, this);
                sendingAgent.sendNextDataRequest();
            }
            catch (IOException e)
            {
                final EFileTransferFailed event = new EFileTransferFailed(this);
                event.setRemote(false);
                event.setReason(e.getMessage());
                fileTransferFailedBus.fireAsync(event);
                sendMessageToPeer(new FileTransferError(e.getMessage()));
            }
        }
        else
        {
            sendingAgent.processMessage(message);
        }
    }
}
