package ru.malnev.gbcloud.common.config;

import org.jetbrains.annotations.NotNull;

import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

/**
 * This interface is implemented by concrete option classes
 *
 * @param <T> Value class
 * @author alexey.malnev
 */
public interface IOption<T> extends Cloneable
{
    /**
     * Get option value
     *
     * @return Option value
     */
    T getValue();

    /**
     * Set option value
     *
     * @param value
     */
    void setValue(final @NotNull T value);

    /**
     * Get option name
     *
     * @return Option name
     */
    String getName();

    /**
     * Set option name
     *
     * @param name Option name
     */
    void setName(final @NotNull String name);

    /**
     * Get default value for this option. This value is assigned when the option
     * fails to load itself from the persistent storage
     *
     * @return Default value for this option.
     */
    T getDefaultValue();

    /**
     * Set default value for this option. This value is assigned when the option
     * fails to load itself from the persistent storage
     *
     * @param defaultValue
     */
    void setDefaultValue(final @NotNull T defaultValue);

    /**
     * Save this option to the specified preference node
     *
     * @param preferences Preference node to save to.
     * @throws BackingStoreException
     */
    void save(final @NotNull Preferences preferences) throws BackingStoreException;

    /**
     * Load this option from the specified preference node
     *
     * @param preferences Preference node to read from
     */
    void load(final @NotNull Preferences preferences);

    /**
     * Clone this option
     *
     * @return Cloned instance
     * @throws CloneNotSupportedException
     */
    IOption<T> clone() throws CloneNotSupportedException;
}
