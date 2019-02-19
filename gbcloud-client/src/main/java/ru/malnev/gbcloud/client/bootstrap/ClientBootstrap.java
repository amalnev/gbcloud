package ru.malnev.gbcloud.client.bootstrap;

import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;
import ru.malnev.gbcloud.client.config.ClientConfig;
import ru.malnev.gbcloud.common.bootstrap.Bootstrap;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class ClientBootstrap extends Bootstrap
{
    @Getter
    @Inject
    private ClientConfig config;

    @Setter
    private String[] commandLineArgs;

    @Override
    @SneakyThrows
    public void run()
    {
        if(commandLineArgs != null) config.parseCommandLine(commandLineArgs);
        super.run();
    }
}
