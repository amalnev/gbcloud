package ru.malnev.gbcloud.common.config;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

/**
 * Generic option implementation.
 *
 * @param <T> Value class
 */
@Setter
@Getter
@NoArgsConstructor
public abstract class Option<T> implements IOption<T>
{
    private T value;
    private String name;
    private T defaultValue;

    public Option(final @NotNull String name, final @NotNull T defaultValue)
    {
        this.name = name;
        this.defaultValue = defaultValue;
    }

    public Option<T> clone() throws CloneNotSupportedException
    {
        return (Option<T>) super.clone();
    }
}
