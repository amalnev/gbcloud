package ru.malnev.gbcloud.client.command;

import ru.malnev.gbcloud.client.conversations.ClientConversationManager;
import ru.malnev.gbcloud.client.conversations.GetClientAgent;
import ru.malnev.gbcloud.common.conversations.ActiveAgent;

import javax.inject.Inject;

@Keyword(Const.GET_COMMAND_KEYWORD)
@Description(Const.GET_COMMAND_DESCRIPTION)
@Arguments(Const.TARGET_FILE_ARGUMENT_NAME)
public class GetCommand extends AbstractCommand
{
    @Inject
    @ActiveAgent
    private GetClientAgent agent;

    @Inject
    private ClientConversationManager conversationManager;

    @Override
    public void run()
    {
        agent.setFileName(getArgumentValue(Const.TARGET_FILE_ARGUMENT_NAME));
        conversationManager.startConversation(agent);
    }
}
