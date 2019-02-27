package ru.malnev.gbcloud.common.conversations;

import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import ru.malnev.gbcloud.common.events.EFileTransferFailed;
import ru.malnev.gbcloud.common.filesystem.IFileReader;
import ru.malnev.gbcloud.common.messages.FileDataAcceptedResponse;
import ru.malnev.gbcloud.common.messages.FileDataRequest;
import ru.malnev.gbcloud.common.messages.FileTransferError;
import ru.malnev.gbcloud.common.messages.IMessage;

import javax.enterprise.event.Event;
import javax.inject.Inject;
import java.io.IOException;
import java.nio.file.Path;

public class FileTransferSendingAgent
{
    @Getter
    @Setter
    private Path filePath;

    @Inject
    private IFileReader fileReader;

    private int lastCseq;

    private int cseq;

    @Inject
    private Event<EFileTransferFailed> fileTransferFailedBus;

    private IConversation conversation;

    public void start(final @NotNull Path filePath,
                      final @NotNull IConversation conversation) throws IOException
    {
        this.filePath = filePath;
        this.conversation = conversation;
        lastCseq = 0;
        fileReader.open(filePath);
    }

    public void stop()
    {
        try
        {
            fileReader.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public void sendNextDataRequest() throws IOException
    {
        final int mtu = conversation.getConversationManager().getTransportChannel().getMTU();
        final byte[] bytesRead = fileReader.read(mtu);
        final boolean lastPackage = bytesRead.length < mtu;
        final FileDataRequest dataRequest = new FileDataRequest(bytesRead, bytesRead.length < mtu, ++cseq);
        conversation.sendMessageToPeer(dataRequest);
        if (lastPackage) lastCseq = cseq;
        conversation.continueConversation();
    }

    public void processMessage(final @NotNull IMessage message)
    {
        try
        {
            if(message instanceof FileTransferError)
            {
                final FileTransferError errorMessage = (FileTransferError) message;
                final EFileTransferFailed event = new EFileTransferFailed(conversation);
                event.setRemote(false);
                event.setReason(errorMessage.getReason());
                fileTransferFailedBus.fireAsync(event);
                return;
            }

            if (message instanceof FileDataAcceptedResponse)
            {
                final FileDataAcceptedResponse response = (FileDataAcceptedResponse) message;

                //если получено подтверждение о получении последнего пакета - завершаем
                if (response.getCseq() == lastCseq) return;
            }

            sendNextDataRequest();
        }
        catch (IOException e)
        {
            final EFileTransferFailed event = new EFileTransferFailed(conversation);
            event.setRemote(false);
            event.setReason(e.getMessage());
            fileTransferFailedBus.fireAsync(event);
            conversation.sendMessageToPeer(new FileTransferError(e.getMessage()));
        }
    }
}
