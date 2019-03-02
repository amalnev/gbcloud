package ru.malnev.gbcloud.client.conversations;

import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;
import ru.malnev.gbcloud.common.conversations.*;
import ru.malnev.gbcloud.common.messages.IMessage;
import ru.malnev.gbcloud.common.messages.transfer.FileDataAcceptedResponse;
import ru.malnev.gbcloud.common.messages.transfer.FileTransferError;
import ru.malnev.gbcloud.common.messages.transfer.FileTransferReady;
import ru.malnev.gbcloud.common.messages.transfer.PutRequest;

import javax.inject.Inject;
import java.nio.file.Path;

@ActiveAgent
@StartsWith(PutRequest.class)
@Expects({FileDataAcceptedResponse.class, FileTransferReady.class, FileTransferError.class})
public class PutClientAgent extends AbstractConversation
{
    @Getter
    @Setter
    private Path relativeFilePath;

    @Getter
    @Setter
    private Path localRoot;

    @Inject
    private FileTransferSendingAgent sendingAgent;

    @Override
    @SneakyThrows
    protected synchronized void beforeStart(@NotNull IMessage initialMessage)
    {
        final PutRequest putRequest = (PutRequest) initialMessage;
        putRequest.setFileName(relativeFilePath.toString());
        sendingAgent.start(localRoot.resolve(relativeFilePath), this);
    }

    @Override
    protected synchronized void beforeFinish()
    {
        sendingAgent.stop();
    }

    @Override
    public synchronized void processMessageFromPeer(final @NotNull IMessage message)
    {
        sendingAgent.processMessage(message);
    }
}
