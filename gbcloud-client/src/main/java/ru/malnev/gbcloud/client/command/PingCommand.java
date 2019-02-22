package ru.malnev.gbcloud.client.command;

import ru.malnev.gbcloud.client.conversations.ClientConversationManager;
import ru.malnev.gbcloud.client.conversations.KeepAliveClientAgent;

import javax.inject.Inject;

@Keyword(Const.PING_COMMAND_KEYWORD)
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
