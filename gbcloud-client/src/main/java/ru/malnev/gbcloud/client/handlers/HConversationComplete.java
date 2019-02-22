package ru.malnev.gbcloud.client.handlers;

import ru.malnev.gbcloud.client.logging.ClientLogger;
import ru.malnev.gbcloud.common.events.EConversationComplete;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.ObservesAsync;
import javax.interceptor.Interceptors;

@ApplicationScoped
@Interceptors(ClientLogger.class)
public class HConversationComplete
{
    private void handleConversationComplete(@ObservesAsync final EConversationComplete event)
    {

    }
}
