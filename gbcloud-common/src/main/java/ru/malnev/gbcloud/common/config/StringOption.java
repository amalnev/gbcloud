package ru.malnev.gbcloud.common.config;

import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;

import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

/**
 * This class represents the string program option
 *
 * @author alexey.malnev
 */
@NoArgsConstructor
public class StringOption extends Option<String>
{
    public StringOption(final @NotNull String name, final @NotNull String defaultValue)
    {
        super(name, defaultValue);
    }

    /**
     * Save this option to the specified preferences node
     *
     * @param preferences Preference node to save to.
     * @throws BackingStoreException
     */
    @Override
    public void save(final @NotNull Preferences preferences) throws BackingStoreException
    {
        preferences.put(getName(), getValue());
        preferences.flush();
    }

    /**
     * Load the option from the given preferences node
     *
     * @param preferences
     */
    @Override
    public void load(final @NotNull Preferences preferences)
    {
        setValue(preferences.get(getName(), getDefaultValue()));
    }

    /**
     * Clone this option
     *
     * @return Cloned instance
     * @throws CloneNotSupportedException
     */
    @Override
    public StringOption clone() throws CloneNotSupportedException
    {
        return (StringOption) super.clone();
    }
}
