package ru.malnev.gbcloud.server.conversations;

import org.jetbrains.annotations.NotNull;
import ru.malnev.gbcloud.common.conversations.RespondsTo;
import ru.malnev.gbcloud.common.messages.*;
import ru.malnev.gbcloud.common.messages.ls.LsRequest;
import ru.malnev.gbcloud.common.messages.ls.LsResponse;

import javax.inject.Inject;
import java.nio.file.Files;

@RespondsTo(LsRequest.class)
public class LsServerAgent extends ServerAgent
{
    @Inject
    private LsResponse response;

    @Override
    public void processMessageFromPeer(@NotNull IMessage message)
    {
        try
        {
            Files.newDirectoryStream(getCurrentDirectory().getCurrentDirectory()).forEach(element ->
                    response.getElements().add(new LsResponse.FilesystemElement(element)));
            sendMessageToPeer(response);
        }
        catch (Exception e)
        {
            sendMessageToPeer(new ServerErrorResponse());
        }
    }
}
