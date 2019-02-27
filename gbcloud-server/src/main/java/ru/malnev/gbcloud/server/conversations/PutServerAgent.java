package ru.malnev.gbcloud.server.conversations;

import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;
import ru.malnev.gbcloud.common.conversations.Expects;
import ru.malnev.gbcloud.common.conversations.FileTransferReceivingAgent;
import ru.malnev.gbcloud.common.conversations.RespondsTo;
import ru.malnev.gbcloud.common.events.EFileTransferFailed;
import ru.malnev.gbcloud.common.messages.*;
import ru.malnev.gbcloud.common.messages.transfer.FileDataRequest;
import ru.malnev.gbcloud.common.messages.transfer.FileTransferError;
import ru.malnev.gbcloud.common.messages.transfer.FileTransferReady;
import ru.malnev.gbcloud.common.messages.transfer.PutRequest;

import javax.enterprise.event.Event;
import javax.inject.Inject;
import java.io.IOException;

@RespondsTo(PutRequest.class)
@Expects({FileDataRequest.class, FileTransferError.class})
public class PutServerAgent extends ServerAgent
{
    @Inject
    private FileTransferReceivingAgent receivingAgent;

    @Inject
    private Event<EFileTransferFailed> fileTransferFailedBus;

    @Override
    protected void beforeFinish()
    {
        receivingAgent.stop();
    }

    @Override
    @SneakyThrows
    public void processMessageFromPeer(@NotNull IMessage message)
    {
        if(message instanceof PutRequest)
        {
            final PutRequest request = (PutRequest) message;
            try
            {
                receivingAgent.start(getCurrentDirectory().resolve(request.getFileName()), this);
                sendMessageToPeer(new FileTransferReady());
                continueConversation();
            }
            catch(IOException e)
            {
                sendMessageToPeer(new FileTransferError(e.getMessage()));
                final EFileTransferFailed event = new EFileTransferFailed(this);
                event.setRemote(true);
                event.setReason(e.getMessage());
                fileTransferFailedBus.fireAsync(event);
            }
        }
        else
        {
            receivingAgent.processMessage(message);
        }
    }
}
