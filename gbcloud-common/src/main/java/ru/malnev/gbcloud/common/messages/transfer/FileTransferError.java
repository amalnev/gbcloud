package ru.malnev.gbcloud.common.messages.transfer;

import lombok.NoArgsConstructor;
import ru.malnev.gbcloud.common.messages.ServerErrorResponse;

@NoArgsConstructor
public class FileTransferError extends ServerErrorResponse
{
    public FileTransferError(final String reason)
    {
        setReason(reason);
    }
}
