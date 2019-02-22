package ru.malnev.gbcloud.client.command;

import ru.malnev.gbcloud.common.transport.INetworkEndpoint;

import javax.inject.Inject;

import static ru.malnev.gbcloud.client.command.Const.CLOSE_COMMAND_KEYWORD;

@Keyword(CLOSE_COMMAND_KEYWORD)
public class CloseCommand extends AbstractCommand
{
    @Inject
    private INetworkEndpoint networkEndpoint;

    @Override
    public void run()
    {
        networkEndpoint.stop();
    }
}
