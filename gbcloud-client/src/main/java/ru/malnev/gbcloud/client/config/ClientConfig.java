package ru.malnev.gbcloud.client.config;

import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import ru.malnev.gbcloud.common.config.BooleanOption;
import ru.malnev.gbcloud.common.config.CommonConfig;
import ru.malnev.gbcloud.common.config.StringOption;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ClientConfig extends CommonConfig
{
    private static final String LOGIN_OPTION_NAME = "Login";
    private static final String PASSWORD_OPTION_NAME = "Password";

    private static final String DEFAULT_LOGIN = "user";
    private static final String DEFAULT_PASSWORD = "user";

    public ClientConfig()
    {
        addAllowedOption(new StringOption(LOGIN_OPTION_NAME, DEFAULT_LOGIN));
        addAllowedOption(new StringOption(PASSWORD_OPTION_NAME, DEFAULT_PASSWORD));
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
}
