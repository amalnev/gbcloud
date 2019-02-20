package ru.malnev.gbcloud.client.handlers;

import ru.malnev.gbcloud.client.logging.ClientLogger;
import ru.malnev.gbcloud.common.events.EConversationTimedOut;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.ObservesAsync;
import javax.interceptor.Interceptors;

@ApplicationScoped
@Interceptors(ClientLogger.class)
public class HConversationTimedOut
{
    private void handleConversationTimedOut(@ObservesAsync final EConversationTimedOut event)
    {

    }
}
