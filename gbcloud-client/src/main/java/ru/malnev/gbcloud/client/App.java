package ru.malnev.gbcloud.client;

import ru.malnev.gbcloud.common.transport.Bootstrap;

import javax.enterprise.inject.se.SeContainerInitializer;
import javax.enterprise.inject.spi.CDI;

public class App
{
    public static void main(String[] args)
    {
        SeContainerInitializer.newInstance().addPackages(App.class).initialize();
        final Bootstrap bootstrap = CDI.current().select(Bootstrap.class).get();
        bootstrap.run();
    }
}
