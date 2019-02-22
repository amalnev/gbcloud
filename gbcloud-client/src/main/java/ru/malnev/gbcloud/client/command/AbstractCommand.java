package ru.malnev.gbcloud.client.command;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

@Command
public abstract class AbstractCommand implements ICommand
{
    @Getter
    @Setter(value = AccessLevel.PROTECTED)
    private String keyword;

    @Getter
    private final List<ICommandArgument> arguments = new LinkedList<>();

    @Override
    public void collectArguments(@Nullable String[] args)
    {
        if(args != null)
        {
            int i;
            for(i = 1; i < args.length; i++)
            {
                if(i <= arguments.size())
                {
                    arguments.get(i - 1).setValue(args[i]);
                }
            }

            for(i = args.length - 1; i < arguments.size(); i++)
            {
                final ICommandArgument argument = arguments.get(i);
                System.out.println("Please type the value for [" + argument.getName() + "]:");
                final Scanner scanner = new Scanner(System.in);
                argument.setValue(scanner.nextLine());
            }
        }
    }

    @Override
    public @Nullable String getArgumentValue(@NotNull String name)
    {
        for (final ICommandArgument argument : arguments)
        {
            if (argument.getName().equals(name))
            {
                return argument.getValue();
            }
        }
        return null;
    }
}
