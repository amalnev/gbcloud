package ru.malnev.gbcloud.client.command;

import lombok.SneakyThrows;
import ru.malnev.gbcloud.common.filesystem.DirectoryWatcher;

import javax.inject.Inject;

@Keyword(Const.WATCH_COMMAND_KEYWORD)
@Description(Const.WATCH_COMMAND_DESCRIPTION)
@Arguments(Const.TARGET_DIRECTORY_ARGUMENT_NAME)
public class WatchCommand extends AbstractCommand
{
    @Inject
    private CLI cli;

    @Inject
    private DirectoryWatcher directoryWatcher;

    @Override
    @SneakyThrows
    public void run()
    {
        directoryWatcher.watchDirectory(cli.getCurrentDirectory().resolve(getArgumentValue(Const.TARGET_DIRECTORY_ARGUMENT_NAME)).toString());
        directoryWatcher.run();
    }
}
