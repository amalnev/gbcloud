package ru.malnev.gbcloud.server.handlers;

import ru.malnev.gbcloud.common.events.EConversationTimedOut;
import ru.malnev.gbcloud.server.logging.ServerLogger;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.ObservesAsync;
import javax.interceptor.Interceptors;

@ApplicationScoped
@Interceptors(ServerLogger.class)
public class HConversationTimedOut
{
    private void handleConversationTimedOut(@ObservesAsync final EConversationTimedOut event)
    {

    }
}
