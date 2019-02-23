package ru.malnev.gbcloud.client.command;

import ru.malnev.gbcloud.client.config.ClientConfig;
import ru.malnev.gbcloud.common.transport.INetworkEndpoint;

import javax.inject.Inject;

import static ru.malnev.gbcloud.client.command.Const.LOGIN_ARGUMENT_NAME;
import static ru.malnev.gbcloud.client.command.Const.PASSWORD_ARGUMENT_NAME;
import static ru.malnev.gbcloud.client.command.Const.SERVER_ARGUMENT_NAME;

@Keyword(Const.OPEN_COMMAND_KEYWORD)
@Description(Const.OPEN_COMMAND_DESCRIPTION)
@Arguments({SERVER_ARGUMENT_NAME, LOGIN_ARGUMENT_NAME, PASSWORD_ARGUMENT_NAME})
public class OpenCommand extends AbstractCommand
{
    @Inject
    private ClientConfig config;

    @Inject
    private INetworkEndpoint networkEndpoint;

    @Override
    public void run()
    {
        config.setServerAddress(getArgumentValue(SERVER_ARGUMENT_NAME));
        config.setLogin(getArgumentValue(LOGIN_ARGUMENT_NAME));
        config.setPassword(getArgumentValue(PASSWORD_ARGUMENT_NAME));

        networkEndpoint.start();
    }
}
