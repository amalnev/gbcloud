package ru.malnev.gbcloud.client.conversations;

import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import ru.malnev.gbcloud.client.command.CLI;
import ru.malnev.gbcloud.common.conversations.AbstractConversation;
import ru.malnev.gbcloud.common.conversations.ActiveAgent;
import ru.malnev.gbcloud.common.conversations.Expects;
import ru.malnev.gbcloud.common.conversations.StartsWith;
import ru.malnev.gbcloud.common.messages.IMessage;
import ru.malnev.gbcloud.common.messages.ServerOkResponse;
import ru.malnev.gbcloud.common.messages.cd.CdFailResponse;
import ru.malnev.gbcloud.common.messages.cd.CdRequest;

import javax.inject.Inject;
import java.nio.file.Paths;

@ActiveAgent
@StartsWith(CdRequest.class)
@Expects({ServerOkResponse.class, CdFailResponse.class})
public class CdClientAgent extends AbstractConversation
{
    private static final String FAIL_MESSAGE = "Remote change dir failed. Reason: ";

    @Inject
    private CLI cli;

    @Getter
    @Setter
    private String targetDirectory;

    @Override
    protected void beforeStart(@NotNull IMessage initialMessage)
    {
        final CdRequest cdRequest = (CdRequest) initialMessage;
        cdRequest.setTargetDirectory(targetDirectory);
    }

    @Override
    public void processMessageFromPeer(@NotNull IMessage message)
    {
        if (message instanceof CdFailResponse)
        {
            final CdFailResponse cdFailResponse = (CdFailResponse) message;
            System.out.println();
            System.out.println(FAIL_MESSAGE + cdFailResponse.getReason());
        }
        else if(message instanceof ServerOkResponse)
        {
            cli.setRemoteDirectory(Paths.get(targetDirectory).normalize().toString());
            cli.updatePrompt();
        }
    }
}
