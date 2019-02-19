package ru.malnev.gbcloud.common.config;

import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

/**
 * Program option repository implementation. Uses Preferences API as a persistent storage.
 * In-memory state of program options is synchronized with the storage on every put operation
 *
 * @author alexey.malnev
 */
public class OptionRepository implements IOptionRepository
{
    private final Preferences preferences;

    private final HashMap<String, IOption> repository = new HashMap<>();

    //@SneakyThrows
    public OptionRepository()
    {
        preferences = Preferences.userNodeForPackage(this.getClass());
    }

    @Override
    @SneakyThrows
    public IOption put(final @NotNull String name, final @NotNull IOption option)
    {
        //возможны только разрешенные опции
        if (!repository.containsKey(name))
            throw new RuntimeException();

        option.save(preferences);
        return repository.put(name, option);
    }

    @Override
    public void addAllowedOption(final @NotNull IOption allowedOption)
    {
        allowedOption.load(preferences);
        try
        {
            allowedOption.save(preferences);
            repository.put(allowedOption.getName(), allowedOption.clone());
        }
        catch (CloneNotSupportedException | BackingStoreException e)
        {
        }
    }

    @Override
    public IOption get(final @NotNull String name)
    {
        return repository.get(name);
    }

    public static class OptionIterator implements Iterator<IOption>
    {
        Iterator<Map.Entry<String, IOption>> iterator;

        public OptionIterator(Iterator<Map.Entry<String, IOption>> iterator)
        {
            this.iterator = iterator;
        }

        @Override
        public boolean hasNext()
        {
            return iterator.hasNext();
        }

        @Override
        public IOption next()
        {
            return iterator.next().getValue();
        }
    }

    @Override
    public Iterator<IOption> iterator()
    {
        return new OptionIterator(repository.entrySet().iterator());
    }
}
