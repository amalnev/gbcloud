package ru.malnev.gbcloud.client.handlers;

import org.jetbrains.annotations.NotNull;
import ru.malnev.gbcloud.common.events.EFileTransferProgress;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.ObservesAsync;
import java.util.HashMap;
import java.util.Map;

@ApplicationScoped
public class HFileTransferProgress
{
    private Map<String, Integer> lastReportedMap = new HashMap<>();

    public synchronized void reset(final @NotNull String fileName)
    {
        lastReportedMap.remove(fileName);
    }

    private synchronized void handleFileTransferProgress(@ObservesAsync final EFileTransferProgress event)
    {
        //Выводим сообщения о прогрессе передачи файла по шкале 25% - 50% - 75%
        Integer lastReported = lastReportedMap.computeIfAbsent(event.getFileName(), k -> 0);

        boolean needsReporting = (event.getPercentComplete() >= 25 && event.getPercentComplete() < 50 && lastReported < 25) ||
                (event.getPercentComplete() >= 50 && event.getPercentComplete() < 75 && lastReported < 50) ||
                (event.getPercentComplete() >= 75 && lastReported < 75);

        if(needsReporting)
        {
            lastReported = event.getPercentComplete();
            if(lastReported >= 25 && lastReported < 50)
                lastReported = 25;
            else if(lastReported >= 50 && lastReported < 75)
                lastReported = 50;
            else if(lastReported >= 75)
                lastReported = 75;

            lastReportedMap.put(event.getFileName(), lastReported);
            System.out.println("Transfer of " + event.getFileName() + " is " + lastReported + "% complete");
        }
    }
}
