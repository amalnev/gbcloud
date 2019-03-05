package ru.malnev.gbcloud.client.command;

import ru.malnev.gbcloud.common.transport.INetworkEndpoint;

import javax.inject.Inject;

import static ru.malnev.gbcloud.client.command.Const.CLOSE_COMMAND_KEYWORD;

@Importance(5)
@Keyword(CLOSE_COMMAND_KEYWORD)
@Description(Const.CLOSE_COMMAND_DESCRIPTION)
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
