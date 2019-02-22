package ru.malnev.gbcloud.client.handlers;

import ru.malnev.gbcloud.client.events.EAuthSuccess;
import ru.malnev.gbcloud.client.logging.ClientLogger;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.ObservesAsync;
import javax.interceptor.Interceptors;

@ApplicationScoped
@Interceptors(ClientLogger.class)
public class HAuthSuccess
{
    private void handleAuthSuccess(@ObservesAsync final EAuthSuccess event)
    {

    }
}
