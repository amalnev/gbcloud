package ru.malnev.gbcloud.client.conversations;

import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;
import ru.malnev.gbcloud.common.conversations.*;
import ru.malnev.gbcloud.common.events.EFileTransferFailed;
import ru.malnev.gbcloud.common.filesystem.IFileReader;
import ru.malnev.gbcloud.common.messages.*;

import javax.enterprise.event.Event;
import javax.inject.Inject;
import java.io.IOException;
import java.nio.file.Path;

@ActiveAgent
@StartsWith(PutRequest.class)
@Expects({FileDataAcceptedResponse.class, FileTransferReady.class, FileTransferError.class})
public class PutClientAgent extends AbstractConversation
{
    @Getter
    @Setter
    private Path filePath;

    @Inject
    private FileTransferSendingAgent sendingAgent;

    @Override
    @SneakyThrows
    protected synchronized void beforeStart(@NotNull IMessage initialMessage)
    {
        final PutRequest putRequest = (PutRequest) initialMessage;
        putRequest.setFileName(filePath.getFileName().toString());
        sendingAgent.start(filePath, this);
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
