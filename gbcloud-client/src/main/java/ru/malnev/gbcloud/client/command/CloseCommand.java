package ru.malnev.gbcloud.client.command;

import ru.malnev.gbcloud.common.transport.INetworkEndpoint;

import javax.inject.Inject;

@Keyword("close")
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
