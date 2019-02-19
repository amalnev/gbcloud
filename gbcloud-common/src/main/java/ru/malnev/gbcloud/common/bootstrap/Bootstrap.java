package ru.malnev.gbcloud.common.bootstrap;

import lombok.Getter;
import ru.malnev.gbcloud.common.transport.INetworkEndpoint;
import ru.malnev.gbcloud.common.transport.Netty;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class Bootstrap
{
    @Netty
    @Inject
    @Getter
    private INetworkEndpoint networkEndpoint;

    public void run()
    {
        networkEndpoint.start();
    }

    public void stop()
    {
        networkEndpoint.stop();
    }
}
