package ru.malnev.gbcloud.client.command;

import ru.malnev.gbcloud.client.conversations.ClientConversationManager;
import ru.malnev.gbcloud.client.conversations.MkdirClientAgent;
import ru.malnev.gbcloud.common.conversations.ActiveAgent;

import javax.inject.Inject;

@Keyword(Const.RMKDIR_COMMAND_KEYWORD)
@Description(Const.RMKDIR_COMMAND_DESCRIPTION)
@Arguments(Const.TARGET_DIRECTORY_ARGUMENT_NAME)
public class RmkdirCommand extends AbstractCommand
{
    @Inject
    @ActiveAgent
    private MkdirClientAgent agent;

    @Inject
    private ClientConversationManager conversationManager;

    @Override
    public void run()
    {
        agent.setDirectoryPath(getArgumentValue(Const.TARGET_DIRECTORY_ARGUMENT_NAME));
        conversationManager.startConversation(agent);
    }
}
