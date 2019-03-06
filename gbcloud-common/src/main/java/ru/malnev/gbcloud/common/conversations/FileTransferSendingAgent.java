package ru.malnev.gbcloud.common.conversations;

import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import ru.malnev.gbcloud.common.events.EFileTransferComplete;
import ru.malnev.gbcloud.common.events.EFileTransferFailed;
import ru.malnev.gbcloud.common.events.EFileTransferProgress;
import ru.malnev.gbcloud.common.filesystem.IFileReader;
import ru.malnev.gbcloud.common.messages.IMessage;
import ru.malnev.gbcloud.common.messages.transfer.FileDataAcceptedResponse;
import ru.malnev.gbcloud.common.messages.transfer.FileDataRequest;
import ru.malnev.gbcloud.common.messages.transfer.FileTransferError;

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

    private int mtu;

    private long fileSize;

    @Inject
    private Event<EFileTransferFailed> fileTransferFailedBus;

    @Inject
    private Event<EFileTransferProgress> fileTransferProgressBus;

    @Inject
    private Event<EFileTransferComplete> fileTransferCompleteBus;

    private IConversation conversation;

    public void start(final @NotNull Path filePath,
                      final @NotNull IConversation conversation) throws IOException
    {
        this.filePath = filePath;
        this.conversation = conversation;
        lastCseq = 0;
        cseq = 0;
        fileReader.open(filePath);
        mtu = conversation.getConversationManager().getTransportChannel().getMTU();
        fileSize = fileReader.getSize();
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
        final byte[] bytesRead = fileReader.read(mtu);
        final boolean lastPackage = bytesRead.length < mtu;
        cseq++;
        long maxCseq = fileSize / mtu;
        int percentComplete = (maxCseq != 0) ? 100 * cseq / (int) maxCseq : 100;
        final FileDataRequest dataRequest = new FileDataRequest(bytesRead, bytesRead.length < mtu, ++cseq, percentComplete);
        conversation.sendMessageToPeer(dataRequest);
        if (lastPackage) lastCseq = cseq;
        conversation.continueConversation();
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

            if (message instanceof FileDataAcceptedResponse)
            {
                final FileDataAcceptedResponse response = (FileDataAcceptedResponse) message;

                long maxCseq = fileSize / mtu;
                int percentComplete = (maxCseq != 0) ? 100 * response.getCseq() / (int) maxCseq : 100;
                fileTransferProgressBus.fireAsync(new EFileTransferProgress(percentComplete, filePath.getFileName().toString()));

                //если получено подтверждение о получении последнего пакета - завершаем
                if (response.getCseq() == lastCseq)
                {
                    fileTransferCompleteBus.fireAsync(new EFileTransferComplete(filePath.getFileName().toString()));
                    return;
                }
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
