package ru.malnev.gbcloud.client.command;


import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface ICommandArgument
{
    @NotNull
    String getName();

    @Nullable
    String getValue();

    void setValue(@Nullable String value);
}
