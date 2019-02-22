package ru.malnev.gbcloud.client.command;

import ru.malnev.gbcloud.client.config.ClientConfig;
import ru.malnev.gbcloud.common.transport.INetworkEndpoint;

import javax.inject.Inject;

@Keyword("open")
public class OpenCommand extends AbstractCommand
{
    private static final String SERVER_ARGUMENT_NAME = "Host";
    private static final String LOGIN_ARGUMENT_NAME = "Login";
    private static final String PASSWORD_ARGUMENT_NAME = "Password";

    @Inject
    private ClientConfig config;

    @Inject
    private INetworkEndpoint networkEndpoint;

    public OpenCommand()
    {
        getArguments().add(new Argument(SERVER_ARGUMENT_NAME, null));
        getArguments().add(new Argument(LOGIN_ARGUMENT_NAME, null));
        getArguments().add(new Argument(PASSWORD_ARGUMENT_NAME, null));
    }

    @Override
    public void run()
    {
        config.setServerAddress(getArgumentValue(SERVER_ARGUMENT_NAME));
        config.setLogin(getArgumentValue(LOGIN_ARGUMENT_NAME));
        config.setPassword(getArgumentValue(PASSWORD_ARGUMENT_NAME));

        networkEndpoint.start();
    }
}
