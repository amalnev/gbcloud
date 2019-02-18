package ru.malnev.gbcloud.client;

import ru.malnev.gbcloud.client.impl.NioClient;
import ru.malnev.gbcloud.common.impl.Bootstrap;
import ru.malnev.gbcloud.common.transport.INetworkEndpoint;
import ru.malnev.gbcloud.common.transport.Nio;

import javax.enterprise.inject.se.SeContainerInitializer;
import javax.enterprise.inject.spi.CDI;
import javax.inject.Inject;

public class App
{
    public static void main(String[] args)
    {
        SeContainerInitializer.newInstance().addPackages(App.class).initialize();
        final Bootstrap bootstrap = CDI.current().select(Bootstrap.class).get();
        bootstrap.run();
    }
}
