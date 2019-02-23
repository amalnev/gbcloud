package ru.malnev.gbcloud.client.command;

import ru.malnev.gbcloud.common.transport.INetworkEndpoint;

import javax.enterprise.inject.Default;
import javax.inject.Inject;

@Keyword(Const.EXIT_COMMAND_KEYWORD)
@Description(Const.EXIT_COMMAND_DESCRIPTION)
public class ExitCommand extends AbstractCommand
{
    @Inject
    private CLI cli;

    @Inject
    private INetworkEndpoint networkEndpoint;

    @Override
    public void run()
    {
        networkEndpoint.stop();
        cli.setActive(false);
    }
}
