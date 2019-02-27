package ru.malnev.gbcloud.common.bootstrap;

import lombok.SneakyThrows;
import ru.malnev.gbcloud.common.config.CommonConfig;

import javax.enterprise.context.ApplicationScoped;
import java.util.logging.Level;
import java.util.logging.Logger;

@ApplicationScoped
public abstract class Bootstrap
{
    @SneakyThrows
    public void run(final String[] commandLineArgs)
    {
        getConfig().parseCommandLine(commandLineArgs);
        final Level logLevel = getConfig().isLoggingEnabled() ? Level.INFO : Level.OFF;
        Logger.getGlobal().setLevel(logLevel);
        init();
    }

    protected abstract CommonConfig getConfig();

    protected abstract void init();
}
