package ru.malnev.gbcloud.client.command;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;
import ru.malnev.gbcloud.client.config.ClientConfig;
import ru.malnev.gbcloud.common.filesystem.PathDoesNotExistException;
import ru.malnev.gbcloud.common.filesystem.PathIsNotADirectoryException;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

@ApplicationScoped
public class CLI
{
    private static final String UNRECOGNIZED_COMMAND_MESSAGE = "Unrecognized command";

    private static class EConsoleInputRequired{}

    @AllArgsConstructor
    private static class EConsoleInputReceived
    {
        @Getter
        @Setter
        private String line;
    }

    @Inject
    private Event<EConsoleInputRequired> consoleInputRequired;

    @Inject
    private Event<EConsoleInputReceived> consoleInputReceived;

    @Inject
    private ICommandParser parser;

    @Setter
    @Getter
    private boolean active = true;

    private Path currentDirectory;

    @Inject
    private ClientConfig config;

    private void handleConsoleInputRequired(@Observes final EConsoleInputRequired event)
    {
        final Scanner scanner = new Scanner(System.in);
        consoleInputReceived.fire(new EConsoleInputReceived(scanner.nextLine()));
    }

    private void handleConsoleInputReceived(@Observes final EConsoleInputReceived event)
    {
        final ICommand command = parser.parse(event.getLine());
        try
        {
            if(command != null)
            {
                command.run();
            }
            else
            {
                System.out.println(UNRECOGNIZED_COMMAND_MESSAGE);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        if(active)
        {
            consoleInputRequired.fire(new EConsoleInputRequired());
        }
    }

    public void start()
    {
        currentDirectory = Paths.get(config.getLocalStorage()).toAbsolutePath().normalize();
        consoleInputRequired.fire(new EConsoleInputRequired());
    }

    public String pwd()
    {
        return currentDirectory.toString();
    }

    public void cd(final @NotNull String path) throws PathDoesNotExistException, PathIsNotADirectoryException
    {
        if(path.length() == 0) return;
        Path newPath = Paths.get(path).normalize();
        if(!newPath.isAbsolute()) newPath = currentDirectory.resolve(newPath);
        if(!Files.exists(newPath)) throw new PathDoesNotExistException();
        if(!Files.isDirectory(newPath)) throw new PathIsNotADirectoryException();
        currentDirectory = newPath;
    }
}
