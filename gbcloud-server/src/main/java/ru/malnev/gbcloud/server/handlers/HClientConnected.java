package ru.malnev.gbcloud.server.handlers;


import ru.malnev.gbcloud.server.events.EClientConntected;
import ru.malnev.gbcloud.server.logging.ServerLogger;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.ObservesAsync;
import javax.interceptor.Interceptors;

@ApplicationScoped
@Interceptors(ServerLogger.class)
public class HClientConnected
{
    private void handleClientConnected(@ObservesAsync final EClientConntected event)
    {

    }
}
