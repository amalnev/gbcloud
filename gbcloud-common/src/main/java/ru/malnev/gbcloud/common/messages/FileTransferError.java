package ru.malnev.gbcloud.common.messages;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class FileTransferError extends ServerErrorResponse
{
    public FileTransferError(final String reason)
    {
        setReason(reason);
    }
}
