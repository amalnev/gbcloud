package ru.malnev.gbcloud.client.command;

import lombok.SneakyThrows;

import javax.inject.Inject;
import java.io.IOException;
import java.nio.file.Files;

@Keyword(Const.LLS_COMMAND_KEYWORD)
@Description(Const.LLS_COMMAND_DESCRIPTION)
public class LlsCommand extends AbstractCommand
{
    @Inject
    private CLI cli;

    @Override
    @SneakyThrows
    public void run()
    {
        Files.list(cli.getCurrentDirectory())
                .filter(element -> Files.isDirectory(element))
                .forEach(directory -> System.out.println("<DIR>\t\t\t\t\t" + directory.getName(directory.getNameCount() - 1)));
        Files.list(cli.getCurrentDirectory())
                .filter(element -> !Files.isDirectory(element))
                .forEach(file ->
                {
                    try
                    {
                        System.out.println("\t\t" + Files.size(file) + "\t\t\t" + file.getFileName());
                    }
                    catch (IOException e)
                    {
                        e.printStackTrace();
                    }
                });
    }
}
