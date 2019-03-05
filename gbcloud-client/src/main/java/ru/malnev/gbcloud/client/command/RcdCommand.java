package ru.malnev.gbcloud.client.command;

import ru.malnev.gbcloud.client.conversations.CdClientAgent;
import ru.malnev.gbcloud.client.conversations.ClientConversationManager;
import ru.malnev.gbcloud.common.conversations.ActiveAgent;

import javax.inject.Inject;

import static ru.malnev.gbcloud.client.command.Const.TARGET_DIRECTORY_ARGUMENT_NAME;

@Importance(2)
@Keyword(Const.RCD_COMMAND_KEYWORD)
@Arguments(TARGET_DIRECTORY_ARGUMENT_NAME)
@Description(Const.RCD_COMMAND_DESCRIPTION)
public class RcdCommand extends AbstractCommand
{
    @Inject
    @ActiveAgent
    private CdClientAgent agent;

    @Inject
    private ClientConversationManager conversationManager;

    @Override
    public void run()
    {
        final String targetDirectory = getArgumentValue(TARGET_DIRECTORY_ARGUMENT_NAME);
        agent.setTargetDirectory(targetDirectory);
        conversationManager.startConversation(agent);
    }
}
