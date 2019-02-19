package ru.malnev.gbcloud.client.handlers;

import ru.malnev.gbcloud.client.bootstrap.ClientBootstrap;
import ru.malnev.gbcloud.client.events.EAuthFailure;
import ru.malnev.gbcloud.client.logging.ClientLogger;
import ru.malnev.gbcloud.common.transport.INetworkEndpoint;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.ObservesAsync;
import javax.inject.Inject;
import javax.interceptor.Interceptors;

@ApplicationScoped
@Interceptors(ClientLogger.class)
public class HAuthFailure
{
    @Inject
    private ClientBootstrap bootstrap;

    private void handleAuthFailure(@ObservesAsync final EAuthFailure event)
    {
        bootstrap.stop();
        System.exit(0);
    }
}
