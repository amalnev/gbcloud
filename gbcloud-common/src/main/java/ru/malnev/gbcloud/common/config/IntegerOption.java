package ru.malnev.gbcloud.common.config;

import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;

import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

/**
 * This class represents an integer program option
 *
 * @author alexey.malnev
 */
@NoArgsConstructor
public class IntegerOption extends Option<Integer>
{
    public IntegerOption(final @NotNull String name, final @NotNull Integer defaultValue)
    {
        super(name, defaultValue);
    }

    /**
     * Save the option to the specified preference node
     *
     * @param preferences Preference no to save option to
     * @throws BackingStoreException
     */
    @Override
    public void save(final @NotNull Preferences preferences) throws BackingStoreException
    {
        preferences.putInt(getName(), getValue());
        preferences.flush();
    }

    /**
     * Load the option from the specified preference node
     *
     * @param preferences Preference node, containing the option
     */
    @Override
    public void load(final @NotNull Preferences preferences)
    {
        setValue(preferences.getInt(getName(), getDefaultValue()));
    }

    /**
     * Clone this option
     *
     * @return Cloned instance
     * @throws CloneNotSupportedException
     */
    @Override
    public IntegerOption clone() throws CloneNotSupportedException
    {
        return (IntegerOption) super.clone();
    }
}
