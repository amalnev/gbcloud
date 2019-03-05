package ru.malnev.gbcloud.client.handlers;

import ru.malnev.gbcloud.common.events.EFileTransferComplete;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.ObservesAsync;
import javax.inject.Inject;

@ApplicationScoped
public class HFileTransferComplete
{
    @Inject
    private HFileTransferProgress transferProgress;

    private void handleFileTransferComplete(@ObservesAsync final EFileTransferComplete event)
    {
        System.out.println("Transfer of " + event.getFileName() + " is 100% complete.");
        transferProgress.reset(event.getFileName());
    }
}
