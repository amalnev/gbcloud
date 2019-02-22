package ru.malnev.gbcloud.server.handlers;

import ru.malnev.gbcloud.common.events.EConversationComplete;
import ru.malnev.gbcloud.server.logging.ServerLogger;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.ObservesAsync;
import javax.interceptor.Interceptors;

@ApplicationScoped
@Interceptors(ServerLogger.class)
public class HConversationComplete
{
    private void handleConversationComplete(@ObservesAsync final EConversationComplete event)
    {

    }
}
