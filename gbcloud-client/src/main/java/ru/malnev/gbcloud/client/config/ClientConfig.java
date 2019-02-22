package ru.malnev.gbcloud.client.config;

import org.jetbrains.annotations.NotNull;
import ru.malnev.gbcloud.common.config.CommonConfig;
import ru.malnev.gbcloud.common.config.StringOption;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ClientConfig extends CommonConfig
{
    private static final String SERVER_ADDRESS_OPTION_NAME = "Server";
    private static final String LOGIN_OPTION_NAME = "Login";
    private static final String PASSWORD_OPTION_NAME = "Password";
    private static final String LOCAL_STORAGE_OPTION_NAME = "LocalStorage";

    private static final String DEFAULT_SERVER_ADDRESS = "localhost";
    private static final String DEFAULT_LOGIN = "user";
    private static final String DEFAULT_PASSWORD = "user";
    private static final String DEFAULT_LOCAL_STORAGE = "./client";

    public ClientConfig()
    {
        addAllowedOption(new StringOption(SERVER_ADDRESS_OPTION_NAME, DEFAULT_SERVER_ADDRESS));
        addAllowedOption(new StringOption(LOGIN_OPTION_NAME, DEFAULT_LOGIN));
        addAllowedOption(new StringOption(PASSWORD_OPTION_NAME, DEFAULT_PASSWORD));
        addAllowedOption(new StringOption(LOCAL_STORAGE_OPTION_NAME, DEFAULT_LOCAL_STORAGE));
    }

    @NotNull
    public String getServerAddress()
    {
        return getStringOption(SERVER_ADDRESS_OPTION_NAME);
    }

    public void setServerAddress(final @NotNull String serverAddress)
    {
        setStringOption(SERVER_ADDRESS_OPTION_NAME, serverAddress, DEFAULT_SERVER_ADDRESS);
    }

    @NotNull
    public String getLogin()
    {
        return getStringOption(LOGIN_OPTION_NAME);
    }

    public void setLogin(final @NotNull String login)
    {
        setStringOption(LOGIN_OPTION_NAME, login, DEFAULT_LOGIN);
    }

    @NotNull
    public String getPassword()
    {
        return getStringOption(PASSWORD_OPTION_NAME);
    }

    public void setPassword(final @NotNull String password)
    {
        setStringOption(PASSWORD_OPTION_NAME, password, DEFAULT_PASSWORD);
    }

    @NotNull
    public String getLocalStorage()
    {
        return getStringOption(LOCAL_STORAGE_OPTION_NAME);
    }

    public void setLocalStorage(final @NotNull String localStorage)
    {
        setStringOption(LOCAL_STORAGE_OPTION_NAME, localStorage, DEFAULT_LOCAL_STORAGE);
    }
}
