package ru.malnev.gbcloud.server.conversations;

import org.jetbrains.annotations.NotNull;
import ru.malnev.gbcloud.common.messages.IMessage;
import ru.malnev.gbcloud.common.messages.LsRequest;
import ru.malnev.gbcloud.common.messages.LsResponse;
import ru.malnev.gbcloud.common.messages.ServerErrorResponse;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class LsServerAgent extends ServerAgent
{
    public LsServerAgent()
    {
        expectMessage(LsRequest.class);
        setTimeoutMillis(60000);
    }

    @Override
    public void processMessageFromPeer(@NotNull IMessage message)
    {
        try
        {
            final Path userHomeDir = Paths.get(getUser().getHomeDirectory());
            final Path targetPath = userHomeDir.resolve(((LsRequest) message).getRequestedPath());
            final LsResponse response = new LsResponse();
            response.setRequestedPath(((LsRequest) message).getRequestedPath());
            if (!Files.exists(targetPath))
            {
                response.setExisting(false);
            }
            else
            {
                response.setExisting(true);
                if (Files.isDirectory(targetPath))
                {
                    Files.newDirectoryStream(targetPath).forEach(element ->
                            response.getElements().add(new LsResponse.FilesystemElement(element)));
                }
                else
                {
                    response.getElements().add(new LsResponse.FilesystemElement(targetPath));
                }
            }
            sendMessageToPeer(response);
        }
        catch (Exception e)
        {
            sendMessageToPeer(new ServerErrorResponse());
        }
        finally
        {
            getConversationManager().stopConversation(this);
        }
    }
}
