package ru.malnev.gbcloud.client.command;

import ru.malnev.gbcloud.client.conversations.ClientConversationManager;
import ru.malnev.gbcloud.client.conversations.KeepAliveClientAgent;

import javax.inject.Inject;

@Keyword(Const.PING_COMMAND_KEYWORD)
@Description(Const.PING_COMMAND_DESCRIPTION)
public class PingCommand extends AbstractCommand
{
    @Inject
    private ClientConversationManager conversationManager;

    @Override
    public void run()
    {
        conversationManager.startConversation(KeepAliveClientAgent.class);
    }
}
