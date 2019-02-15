package ru.malnev.gbcloud.server;

import ru.malnev.gbcloud.common.transport.INetworkEndpoint;
import ru.malnev.gbcloud.server.impl.NioServer;

import javax.enterprise.inject.se.SeContainer;
import javax.enterprise.inject.se.SeContainerInitializer;
import javax.enterprise.inject.spi.CDI;

public class App
{
    public static void main(String[] args)
    {
        final SeContainer container = SeContainerInitializer.newInstance().addPackages(App.class).initialize();

        INetworkEndpoint server = CDI.current().select(NioServer.class).get();
        server.start();
    }
}
