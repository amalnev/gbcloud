package ru.malnev.gbcloud.common.filesystem;

import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;
import ru.malnev.gbcloud.common.events.EDirectoryCreated;
import ru.malnev.gbcloud.common.events.EFileCreated;
import ru.malnev.gbcloud.common.events.EFilesystemItemDeleted;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import java.io.IOException;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;
import java.util.function.Consumer;

@ApplicationScoped
public class DirectoryWatcher implements Runnable
{
    private WatchService watchService;

    private Path watchPath;

    private List<WatchKey> watchKeys;

    @Inject
    private Event<EDirectoryCreated> directoryCreatedBus;

    @Inject
    private Event<EFileCreated> fileCreatedBus;

    @Inject
    private Event<EFilesystemItemDeleted> itemDeletedBus;

    private Map<Path, Long> timestampMap = new HashMap<>();

    @SneakyThrows
    public DirectoryWatcher()
    {
        watchService = FileSystems.getDefault().newWatchService();
        watchKeys = new LinkedList<>();
    }

    public void watchDirectory(final @NotNull String path) throws PathDoesNotExistException,
            PathIsNotADirectoryException, IOException
    {
        watchPath = Paths.get(path).toAbsolutePath().normalize();
        if (!Files.exists(watchPath)) throw new PathDoesNotExistException();
        if (!Files.isDirectory(watchPath)) throw new PathIsNotADirectoryException();

        watchKeys.forEach(WatchKey::cancel);
        watchKeys.clear();

        Files.walk(watchPath)
                .filter(element -> Files.isDirectory(element))
                .forEach(dir ->
                {
                    try
                    {
                        watchKeys.add(dir.register(watchService,
                                StandardWatchEventKinds.ENTRY_CREATE,
                                StandardWatchEventKinds.ENTRY_DELETE,
                                StandardWatchEventKinds.ENTRY_MODIFY));
                    }
                    catch (IOException e)
                    {
                        e.printStackTrace();
                    }
                });
    }

    @SneakyThrows
    @Override
    public void run()
    {
        while (true)
        {
            WatchKey watchKey;
            while ((watchKey = watchService.take()) != null)
            {
                try
                {
                    final Path directoryPath = (Path) watchKey.watchable();
                    for (final WatchEvent<?> event : watchKey.pollEvents())
                    {
                        if (event.kind().equals(StandardWatchEventKinds.OVERFLOW)) return;
                        final Path absoluteLocalFilePath = directoryPath.resolve(((WatchEvent<Path>) event).context());

                        final Consumer<Path> directoryCreatedNotifier = (dir) ->
                        {
                            directoryCreatedBus.fireAsync(new EDirectoryCreated(dir.toString(),
                                    watchPath.relativize(dir).toString(),
                                    watchPath.toString()));
                        };

                        final Consumer<Path> fileCreatedNotifier = (file) ->
                        {
                            fileCreatedBus.fireAsync(new EFileCreated(file.toString(),
                                    watchPath.relativize(file).toString(),
                                    watchPath.toString()));
                        };

                        if (event.kind().equals(StandardWatchEventKinds.ENTRY_CREATE))
                        {
                            if (Files.isDirectory(absoluteLocalFilePath))
                            {
                                try
                                {
                                    Files.walk(absoluteLocalFilePath)
                                            .sorted(Comparator.naturalOrder())
                                            .filter(element -> Files.isDirectory(element))
                                            .forEach(dir ->
                                            {
                                                try
                                                {
                                                    watchKeys.add(dir.register(watchService,
                                                            StandardWatchEventKinds.ENTRY_CREATE,
                                                            StandardWatchEventKinds.ENTRY_DELETE,
                                                            StandardWatchEventKinds.ENTRY_MODIFY));
                                                    directoryCreatedNotifier.accept(dir);
                                                }
                                                catch (IOException e)
                                                {
                                                    e.printStackTrace();
                                                }
                                            });

                                    Files.walk(absoluteLocalFilePath)
                                            .filter(element -> !Files.isDirectory(element))
                                            .forEach(fileCreatedNotifier);
                                }
                                catch (IOException e)
                                {
                                    e.printStackTrace();
                                }
                            }
                            else
                            {
                                fileCreatedNotifier.accept(absoluteLocalFilePath);
                            }
                        }
                        else if (event.kind().equals(StandardWatchEventKinds.ENTRY_DELETE))
                        {
                            if(Files.isDirectory(absoluteLocalFilePath))
                                watchKeys.removeIf(key -> key.watchable().equals(absoluteLocalFilePath));

                            itemDeletedBus.fireAsync(new EFilesystemItemDeleted(
                                    absoluteLocalFilePath.toString(),
                                    watchPath.relativize(absoluteLocalFilePath).toString(),
                                    watchPath.toString()));
                        }
                        else if (event.kind().equals(StandardWatchEventKinds.ENTRY_MODIFY))
                        {
                            if (!Files.isDirectory(absoluteLocalFilePath))
                            {
                                final Long oldTimestamp = timestampMap.get(absoluteLocalFilePath);
                                final Long newTimestamp = absoluteLocalFilePath.toFile().lastModified();
                                if(!newTimestamp.equals(oldTimestamp))
                                {
                                    timestampMap.put(absoluteLocalFilePath, newTimestamp);
                                    fileCreatedNotifier.accept(absoluteLocalFilePath);
                                }
                            }
                        }
                    }
                }
                finally
                {
                    watchKey.reset();
                }
            }
        }
    }
}
