package ru.malnev.gbcloud.common.impl;

import ru.malnev.gbcloud.common.transport.INetworkEndpoint;
import ru.malnev.gbcloud.common.transport.Netty;
import ru.malnev.gbcloud.common.transport.Nio;

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
