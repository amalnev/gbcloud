package ru.malnev.gbcloud.client.command;

import ru.malnev.gbcloud.client.conversations.CdClientAgent;
import ru.malnev.gbcloud.client.conversations.ClientConversationManager;
import ru.malnev.gbcloud.common.conversations.ActiveAgent;
import ru.malnev.gbcloud.common.messages.CdRequest;

import javax.inject.Inject;

@Keyword("rcd")
public class RcdCommand extends AbstractCommand
{
    private final static String TARGET_DIRECTORY_ARGUMENT_NAME = "Target directory";

    @Inject
    @ActiveAgent
    private CdClientAgent agent;

    @Inject
    private ClientConversationManager conversationManager;

    public RcdCommand()
    {
        getArguments().add(new Argument(TARGET_DIRECTORY_ARGUMENT_NAME, null));
    }

    @Override
    public void run()
    {
        final String targetDirectory = getArgumentValue(TARGET_DIRECTORY_ARGUMENT_NAME);
        agent.setTargetDirectory(targetDirectory);
        conversationManager.startConversation(agent);
    }
}
