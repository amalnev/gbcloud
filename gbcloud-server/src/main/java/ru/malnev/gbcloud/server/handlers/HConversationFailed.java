package ru.malnev.gbcloud.server.handlers;

import ru.malnev.gbcloud.common.events.EConversationFailed;
import ru.malnev.gbcloud.server.logging.ServerLogger;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.ObservesAsync;
import javax.interceptor.Interceptors;

@ApplicationScoped
@Interceptors(ServerLogger.class)
public class HConversationFailed
{
    private void handleConversationFailed(@ObservesAsync final EConversationFailed event)
    {

    }
}
