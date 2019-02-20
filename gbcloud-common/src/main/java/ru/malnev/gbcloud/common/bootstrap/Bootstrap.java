package ru.malnev.gbcloud.common.bootstrap;

import lombok.Getter;
import lombok.SneakyThrows;
import ru.malnev.gbcloud.common.config.CommonConfig;
import ru.malnev.gbcloud.common.transport.INetworkEndpoint;
import ru.malnev.gbcloud.common.transport.Netty;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public abstract class Bootstrap
{
    @Netty
    @Inject
    @Getter
    private INetworkEndpoint networkEndpoint;

    @SneakyThrows
    public void run(final String[] commandLineArgs)
    {
        getConfig().parseCommandLine(commandLineArgs);
        init();
        networkEndpoint.start();
    }

    public void stop()
    {
        networkEndpoint.stop();
    }

    protected abstract CommonConfig getConfig();

    protected abstract void init();
}
