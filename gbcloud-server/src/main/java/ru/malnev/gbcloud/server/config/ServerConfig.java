package ru.malnev.gbcloud.server.config;

import org.jetbrains.annotations.NotNull;
import ru.malnev.gbcloud.common.config.CommonConfig;
import ru.malnev.gbcloud.common.config.StringOption;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ServerConfig extends CommonConfig
{
    private static final String ROOT_DIRECTORY_OPTION_NAME = "RootDir";

    private static final String DEFAULT_ROOT_DIRECTORY = "./server";

    public ServerConfig()
    {
        addAllowedOption(new StringOption(ROOT_DIRECTORY_OPTION_NAME, DEFAULT_ROOT_DIRECTORY));
    }

    @NotNull
    public String getRootDirectory()
    {
        return getStringOption(ROOT_DIRECTORY_OPTION_NAME);
    }

    public void setRootDirectory(final @NotNull String rootDirectory)
    {
        setStringOption(ROOT_DIRECTORY_OPTION_NAME, rootDirectory, DEFAULT_ROOT_DIRECTORY);
    }
}
