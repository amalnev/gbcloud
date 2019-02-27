package ru.malnev.gbcloud.client.command;

import ru.malnev.gbcloud.client.conversations.PutClientAgent;
import ru.malnev.gbcloud.common.conversations.ActiveAgent;
import ru.malnev.gbcloud.common.conversations.IConversationManager;

import javax.inject.Inject;
import java.nio.file.Files;
import java.nio.file.Path;

@Keyword(Const.PUT_COMMAND_KEYWORD)
@Description(Const.PUT_COMMAND_DESCRIPTION)
@Arguments(Const.TARGET_FILE_ARGUMENT_NAME)
public class PutCommand extends AbstractCommand
{
    private final static String PATH_DOES_NOT_EXIST_MESSAGE = "Specified path does not exist";

    @Inject
    @ActiveAgent
    private PutClientAgent agent;

    @Inject
    private IConversationManager conversationManager;

    @Inject
    private CLI cli;

    @Override
    public void run()
    {
        final String fileName = getArgumentValue(Const.TARGET_FILE_ARGUMENT_NAME);
        final Path filePath = cli.getCurrentDirectory().resolve(fileName);
        if (!Files.exists(filePath))
        {
            System.out.println(PATH_DOES_NOT_EXIST_MESSAGE);
            return;
        }

        agent.setFilePath(filePath);
        conversationManager.startConversation(agent);
    }
}
