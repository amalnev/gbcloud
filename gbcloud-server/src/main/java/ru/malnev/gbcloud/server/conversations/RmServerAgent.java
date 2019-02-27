package ru.malnev.gbcloud.server.conversations;

import org.jetbrains.annotations.NotNull;
import ru.malnev.gbcloud.common.conversations.RespondsTo;
import ru.malnev.gbcloud.common.messages.IMessage;
import ru.malnev.gbcloud.common.messages.ServerErrorResponse;
import ru.malnev.gbcloud.common.messages.ServerOkResponse;
import ru.malnev.gbcloud.common.messages.rm.RmRequest;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@RespondsTo(RmRequest.class)
public class RmServerAgent extends ServerAgent
{
    @Override
    public void processMessageFromPeer(@NotNull IMessage message)
    {
        //TODO: добавить возможность удалять директории рекурсивно
        final RmRequest request = (RmRequest) message;
        final Path pathToRemove = getCurrentDirectory().resolve(request.getTargetPath());
        try
        {
            Files.delete(pathToRemove);
        }
        catch (IOException e)
        {
            sendMessageToPeer(new ServerErrorResponse(e.getMessage()));
            return;
        }

        sendMessageToPeer(new ServerOkResponse());
    }
}
