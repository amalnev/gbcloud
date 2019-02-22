package ru.malnev.gbcloud.client.command;

import ru.malnev.gbcloud.common.filesystem.PathDoesNotExistException;
import ru.malnev.gbcloud.common.filesystem.PathIsNotADirectoryException;

import javax.inject.Inject;

import static ru.malnev.gbcloud.client.command.Const.TARGET_DIRECTORY_ARGUMENT_NAME;

@Keyword(Const.LCD_COMMAND_KEYWORD)
@Arguments(TARGET_DIRECTORY_ARGUMENT_NAME)
public class LcdCommand extends AbstractCommand
{
    private final static String PATH_DOES_NOT_EXIST_MESSAGE = "Specified path does not exist";
    private final static String PATH_IS_NOT_A_DIR_MESSAGE = "Specified path is not a directory";

    @Inject
    private CLI cli;

    public LcdCommand()
    {
        getArguments().add(new Argument(TARGET_DIRECTORY_ARGUMENT_NAME, null));
    }

    @Override
    public void run()
    {
        try
        {
            final String path = getArgumentValue(TARGET_DIRECTORY_ARGUMENT_NAME);
            if(path == null) return;
            cli.cd(path);
        }
        catch (PathDoesNotExistException e)
        {
            System.out.println(PATH_DOES_NOT_EXIST_MESSAGE);
        }
        catch(PathIsNotADirectoryException e)
        {
            System.out.println(PATH_IS_NOT_A_DIR_MESSAGE);
        }
    }
}
