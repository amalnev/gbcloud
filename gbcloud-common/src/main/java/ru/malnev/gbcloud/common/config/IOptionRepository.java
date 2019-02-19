package ru.malnev.gbcloud.common.config;

import org.jetbrains.annotations.NotNull;

/**
 * Option repository is a container, holding all program options. It has
 * a registry of known (i.e. allowed) options, which has to be initialized prior to
 * getting or setting any values. This can be done by means of {@link IOptionRepository#addAllowedOption}
 * method. After that, {@link IOptionRepository#get} and {@link IOptionRepository#put}
 * methods can be used to query or set values of known options. IOptionRepository implementation internally uses
 * some logic to synchronize in-memory state with persistent storage.
 *
 * @author alexey.malnev
 */
public interface IOptionRepository extends Iterable<IOption>
{
    IOption get(final @NotNull String name);

    IOption put(final @NotNull String name, final @NotNull IOption option);

    void addAllowedOption(final @NotNull IOption allowedOption);
}
