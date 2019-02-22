package ru.malnev.gbcloud.client.command;


import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface ICommand
{
    String getKeyword();

    List<ICommandArgument> getArguments();

    void collectArguments(@Nullable String[] args);

    void run();

    @Nullable String getArgumentValue(@NotNull String name);
}
