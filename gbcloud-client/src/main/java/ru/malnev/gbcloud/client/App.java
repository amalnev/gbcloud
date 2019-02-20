package ru.malnev.gbcloud.client;

import ru.malnev.gbcloud.client.bootstrap.ClientBootstrap;

import javax.enterprise.inject.se.SeContainerInitializer;
import javax.enterprise.inject.spi.CDI;

public class App
{
    public static void main(String[] args)
    {
        SeContainerInitializer.newInstance().addPackages(App.class).initialize();
        final ClientBootstrap bootstrap = CDI.current().select(ClientBootstrap.class).get();
        bootstrap.run(args);
    }
}
