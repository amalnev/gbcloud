package ru.malnev.gbcloud.common.transport;

import javax.inject.Inject;

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
