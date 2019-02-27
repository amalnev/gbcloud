package ru.malnev.gbcloud.server.conversations;

import org.jetbrains.annotations.NotNull;
import ru.malnev.gbcloud.common.conversations.RespondsTo;
import ru.malnev.gbcloud.common.messages.IMessage;
import ru.malnev.gbcloud.common.messages.ServerErrorResponse;
import ru.malnev.gbcloud.common.messages.ServerOkResponse;
import ru.malnev.gbcloud.common.messages.mkdir.MkdirRequest;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@RespondsTo(MkdirRequest.class)
public class MkdirServerAgent extends ServerAgent
{
    @Override
    public void processMessageFromPeer(@NotNull IMessage message)
    {
        final MkdirRequest request = (MkdirRequest) message;
        final Path newPath = getCurrentDirectory().resolve(request.getDirectoryPath());
        try
        {
            Files.createDirectories(newPath);
        }
        catch (IOException e)
        {
            sendMessageToPeer(new ServerErrorResponse(e.getMessage()));
            return;
        }

        sendMessageToPeer(new ServerOkResponse());
    }
}
