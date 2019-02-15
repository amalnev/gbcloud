package ru.malnev.gbcloud.client;

import ru.malnev.gbcloud.client.impl.NioClient;

import javax.enterprise.inject.se.SeContainerInitializer;
import javax.enterprise.inject.spi.CDI;

public class App
{
    public static void main(String[] args)
    {
        SeContainerInitializer.newInstance().addPackages(App.class).initialize();
        final NioClient nioClient = CDI.current().select(NioClient.class).get();
        nioClient.start();
    }
}
