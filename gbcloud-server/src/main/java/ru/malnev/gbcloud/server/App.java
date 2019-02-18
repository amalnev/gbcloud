package ru.malnev.gbcloud.server;

import ru.malnev.gbcloud.server.bootstrap.ServerBootstrap;
import ru.malnev.gbcloud.server.persistence.entitites.User;
import ru.malnev.gbcloud.server.persistence.repositories.UserRepository;

import javax.enterprise.inject.se.SeContainerInitializer;
import javax.enterprise.inject.spi.CDI;

public class App
{
    public static void main(String[] args)
    {
        SeContainerInitializer.newInstance().addPackages(App.class).initialize();

        final ServerBootstrap bootstrap = CDI.current().select(ServerBootstrap.class).get();
        bootstrap.run();
    }
}
