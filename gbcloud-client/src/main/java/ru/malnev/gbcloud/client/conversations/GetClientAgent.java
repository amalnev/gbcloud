package ru.malnev.gbcloud.client.conversations;

import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;
import ru.malnev.gbcloud.client.command.CLI;
import ru.malnev.gbcloud.common.conversations.*;
import ru.malnev.gbcloud.common.messages.IMessage;
import ru.malnev.gbcloud.common.messages.transfer.FileDataRequest;
import ru.malnev.gbcloud.common.messages.transfer.FileTransferError;
import ru.malnev.gbcloud.common.messages.transfer.GetRequest;

import javax.inject.Inject;

@ActiveAgent
@StartsWith(GetRequest.class)
@Expects({FileDataRequest.class, FileTransferError.class})
public class GetClientAgent extends AbstractConversation
{
    @Getter
    @Setter
    private String fileName;

    @Inject
    private CLI cli;

    @Inject
    private FileTransferReceivingAgent receivingAgent;

    @Override
    @SneakyThrows
    protected void beforeStart(@NotNull IMessage initialMessage)
    {
        final GetRequest request = (GetRequest) initialMessage;
        request.setFileName(fileName);
        receivingAgent.start(cli.getCurrentDirectory().resolve(fileName), this);
    }

    @Override
    protected void beforeFinish()
    {
        receivingAgent.stop();
    }

    @Override
    public void processMessageFromPeer(@NotNull IMessage message)
    {
        receivingAgent.processMessage(message);
    }
}
