package ru.malnev.gbcloud.client.handlers;

import ru.malnev.gbcloud.client.command.CLI;
import ru.malnev.gbcloud.client.events.EConnectionClosed;
import ru.malnev.gbcloud.client.logging.ClientLogger;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.ObservesAsync;
import javax.inject.Inject;
import javax.interceptor.Interceptors;

@ApplicationScoped
@Interceptors(ClientLogger.class)
public class HConnectionClosed
{
    @Inject
    private CLI cli;

    private void handleConnectionClosed(@ObservesAsync final EConnectionClosed event)
    {
        System.out.println("Disconnected.");
        cli.resetPrompt();
        cli.updatePrompt();
    }
}
