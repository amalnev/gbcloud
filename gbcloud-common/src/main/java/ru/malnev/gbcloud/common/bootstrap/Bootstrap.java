package ru.malnev.gbcloud.common.bootstrap;

import lombok.SneakyThrows;
import ru.malnev.gbcloud.common.config.CommonConfig;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public abstract class Bootstrap
{
    @SneakyThrows
    public void run(final String[] commandLineArgs)
    {
        getConfig().parseCommandLine(commandLineArgs);
        init();
    }

    protected abstract CommonConfig getConfig();

    protected abstract void init();
}
