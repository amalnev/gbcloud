package ru.malnev.gbcloud.client.handlers;

import ru.malnev.gbcloud.common.events.EFileTransferFailed;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.ObservesAsync;
import javax.inject.Inject;

@ApplicationScoped
public class HFileTransferFailed
{
    @Inject
    private HFileTransferProgress transferProgress;

    private void handleFileTransferFailed(@ObservesAsync final EFileTransferFailed event)
    {
        System.out.println("Transfer of " + event.getFileName() + " failed.");
        transferProgress.reset(event.getFileName());
    }
}
