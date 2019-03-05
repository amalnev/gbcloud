package ru.malnev.gbcloud.client.command;

import ru.malnev.gbcloud.client.conversations.ClientConversationManager;
import ru.malnev.gbcloud.client.conversations.LsClientAgent;

import javax.inject.Inject;

@Importance(2)
@Keyword(Const.RLS_COMMAND_KEYWORD)
@Description(Const.RLS_COMMAND_DESCRIPTION)
public class RlsCommand extends AbstractCommand
{
    @Inject
    private ClientConversationManager conversationManager;

    @Override
    public void run()
    {
        conversationManager.startConversation(LsClientAgent.class);
    }
}
