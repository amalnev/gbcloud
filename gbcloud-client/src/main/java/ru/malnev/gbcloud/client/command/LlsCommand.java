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
        Files.newDirectoryStream(cli.getCurrentDirectory()).forEach(element ->
        {
            try
            {
                final StringBuilder builder = new StringBuilder();
                if (Files.isDirectory(element))
                {
                    builder.append("<DIR>\t\t");
                }
                else
                {
                    builder.append("\t\t");
                    builder.append(Files.size(element));
                    builder.append("\t");
                }
                builder.append(element.getName(element.getNameCount() - 1).toString());
                System.out.println(builder.toString());
            }
            catch (IOException e) {}
        });
    }
}
