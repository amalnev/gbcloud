package ru.malnev.gbcloud.client.command;

import ru.malnev.gbcloud.client.conversations.ClientConversationManager;
import ru.malnev.gbcloud.client.conversations.PwdClientAgent;

import javax.inject.Inject;

@Keyword(Const.RPWD_COMMAND_KEYWORD)
@Description(Const.RPWD_COMMAND_DESCRIPTION)
public class RpwdCommand extends AbstractCommand
{
    @Inject
    private ClientConversationManager conversationManager;

    @Override
    public void run()
    {
        conversationManager.startConversation(PwdClientAgent.class);
    }
}
