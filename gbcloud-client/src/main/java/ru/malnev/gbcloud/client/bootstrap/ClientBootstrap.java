package ru.malnev.gbcloud.client.bootstrap;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.SneakyThrows;
import ru.malnev.gbcloud.client.command.CLI;
import ru.malnev.gbcloud.client.config.ClientConfig;
import ru.malnev.gbcloud.common.bootstrap.Bootstrap;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@ApplicationScoped
public class ClientBootstrap extends Bootstrap
{
    @Inject
    @Getter(AccessLevel.PROTECTED)
    private ClientConfig config;

    @Inject
    private CLI cli;

    @Override
    @SneakyThrows
    protected void init()
    {
        final Path localStorage = Paths.get(config.getLocalStorage());
        if (!Files.exists(localStorage)) Files.createDirectories(localStorage);
        cli.start();
    }
}
