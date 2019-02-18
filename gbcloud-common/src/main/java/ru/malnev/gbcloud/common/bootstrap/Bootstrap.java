package ru.malnev.gbcloud.common.bootstrap;

import ru.malnev.gbcloud.common.transport.INetworkEndpoint;
import ru.malnev.gbcloud.common.transport.Netty;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class Bootstrap
{
    @Netty
    @Inject
    private INetworkEndpoint networkEndpoint;

    public void run()
    {
        networkEndpoint.start();
    }
}
