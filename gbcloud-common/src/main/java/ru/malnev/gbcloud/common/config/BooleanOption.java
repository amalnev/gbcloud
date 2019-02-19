package ru.malnev.gbcloud.common.config;

import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;

import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

/**
 * This class represents the boolean program option
 *
 * @author alexey.malnev
 */
@NoArgsConstructor
public class BooleanOption extends Option<Boolean>
{
    public BooleanOption(final @NotNull String name, final boolean defaultValue)
    {
        super(name, defaultValue);
    }

    /**
     * Save the option to the given preference node
     *
     * @param preferences Preference node to save to
     * @throws BackingStoreException
     */
    @Override
    public void save(final @NotNull Preferences preferences) throws BackingStoreException
    {
        preferences.putBoolean(getName(), getValue());
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
        setValue(preferences.getBoolean(getName(), getDefaultValue()));
    }

    /**
     * Clone this option
     *
     * @return Cloned instance
     * @throws CloneNotSupportedException
     */
    @Override
    public BooleanOption clone() throws CloneNotSupportedException
    {
        return (BooleanOption) super.clone();
    }
}
