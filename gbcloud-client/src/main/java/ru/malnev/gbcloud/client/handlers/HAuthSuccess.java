package ru.malnev.gbcloud.client.handlers;

import ru.malnev.gbcloud.client.conversations.ClientConversationManager;
import ru.malnev.gbcloud.client.conversations.PingWorker;
import ru.malnev.gbcloud.client.events.EAuthSuccess;
import ru.malnev.gbcloud.client.logging.ClientLogger;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.ObservesAsync;
import javax.inject.Inject;
import javax.interceptor.Interceptors;

@ApplicationScoped
@Interceptors(ClientLogger.class)
public class HAuthSuccess
{
    @Inject
    private PingWorker pingWorker;

    private void handleAuthSuccess(@ObservesAsync final EAuthSuccess event)
    {
        pingWorker.start();
    }
}
