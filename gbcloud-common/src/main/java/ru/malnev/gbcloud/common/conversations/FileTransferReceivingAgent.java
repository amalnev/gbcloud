package ru.malnev.gbcloud.common.conversations;

import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import ru.malnev.gbcloud.common.events.EFileTransferFailed;
import ru.malnev.gbcloud.common.filesystem.IFileWriter;
import ru.malnev.gbcloud.common.messages.IMessage;
import ru.malnev.gbcloud.common.messages.transfer.FileDataAcceptedResponse;
import ru.malnev.gbcloud.common.messages.transfer.FileDataRequest;
import ru.malnev.gbcloud.common.messages.transfer.FileTransferError;

import javax.enterprise.event.Event;
import javax.inject.Inject;
import java.io.IOException;
import java.nio.file.Path;

public class FileTransferReceivingAgent
{
    @Inject
    private IFileWriter fileWriter;

    @Getter
    @Setter
    private Path filePath;

    @Inject
    private Event<EFileTransferFailed> fileTransferFailedBus;

    private IConversation conversation;

    public void start(final @NotNull Path filePath,
                      final @NotNull IConversation conversation) throws IOException
    {
        this.conversation = conversation;
        fileWriter.open(filePath);
    }

    public void stop()
    {
        try
        {
            fileWriter.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public void processMessage(final @NotNull IMessage message)
    {
        try
        {
            if (message instanceof FileTransferError)
            {
                final FileTransferError errorMessage = (FileTransferError) message;
                final EFileTransferFailed event = new EFileTransferFailed(conversation);
                event.setRemote(false);
                event.setReason(errorMessage.getReason());
                fileTransferFailedBus.fireAsync(event);
                return;
            }

            final FileDataRequest request = (FileDataRequest) message;
            fileWriter.write(request.getData());
            final FileDataAcceptedResponse response = new FileDataAcceptedResponse();
            response.setCseq(request.getCseq());
            conversation.sendMessageToPeer(response);
            if (!request.isLast()) conversation.continueConversation();
        }
        catch (IOException e)
        {
            conversation.sendMessageToPeer(new FileTransferError(e.getMessage()));
            final EFileTransferFailed event = new EFileTransferFailed(conversation);
            event.setRemote(false);
            event.setReason(e.getMessage());
            fileTransferFailedBus.fireAsync(event);
        }
    }
}
