package ru.malnev.gbcloud.client.command;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Argument implements ICommandArgument
{
    private String name;

    private String value;
}
