package ru.malnev.gbcloud.common.filesystem;

import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;
import ru.malnev.gbcloud.common.events.EDirectoryCreated;
import ru.malnev.gbcloud.common.events.EFileCreated;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.LinkedList;
import java.util.List;

@ApplicationScoped
public class DirectoryWatcher implements Runnable
{
    private static final int QUANTUM = 500;

    private WatchService watchService;

    private Path watchPath;

    private List<WatchKey> watchKeys;

    @Inject
    private Event<EDirectoryCreated> directoryCreatedBus;

    @Inject
    private Event<EFileCreated> fileCreatedBus;

    @SneakyThrows
    public DirectoryWatcher()
    {
        watchService = FileSystems.getDefault().newWatchService();
        watchKeys = new LinkedList<>();
    }

    public synchronized void watchDirectory(final @NotNull String path) throws PathDoesNotExistException,
            PathIsNotADirectoryException, IOException
    {
        watchPath = Paths.get(path).toAbsolutePath().normalize();
        if (!Files.exists(watchPath)) throw new PathDoesNotExistException();
        if (!Files.isDirectory(watchPath)) throw new PathIsNotADirectoryException();

        watchKeys.forEach(WatchKey::cancel);
        watchKeys.clear();

        Files.walkFileTree(watchPath, new SimpleFileVisitor<Path>()
        {
            @Override
            public FileVisitResult preVisitDirectory(final Path dir,
                                                     final BasicFileAttributes attrs) throws IOException
            {
                watchKeys.add(dir.register(watchService,
                        StandardWatchEventKinds.ENTRY_CREATE,
                        StandardWatchEventKinds.ENTRY_DELETE,
                        StandardWatchEventKinds.ENTRY_MODIFY));
                return FileVisitResult.CONTINUE;
            }
        });
    }

    @Override
    public void run()
    {
        while (true)
        {
            synchronized (this)
            {
                WatchKey watchKey;
                while ((watchKey = watchService.poll()) != null)
                {
                    final Path directoryPath = (Path) watchKey.watchable();
                    for (final WatchEvent<?> event : watchKey.pollEvents())
                    {
                        if (event.kind().equals(StandardWatchEventKinds.OVERFLOW)) return;
                        final Path absoluteLocalFilePath = directoryPath.resolve(((WatchEvent<Path>) event).context());
                        final Path relativeLocalFilePath = watchPath.relativize(absoluteLocalFilePath);

                        if (event.kind().equals(StandardWatchEventKinds.ENTRY_CREATE))
                        {
                            if (Files.isDirectory(absoluteLocalFilePath))
                            {
                                try
                                {
                                    Files.walkFileTree(absoluteLocalFilePath, new SimpleFileVisitor<Path>()
                                    {
                                        @Override
                                        public FileVisitResult preVisitDirectory(final Path dir,
                                                                                 final BasicFileAttributes attrs) throws IOException
                                        {
                                            watchKeys.add(dir.register(watchService,
                                                    StandardWatchEventKinds.ENTRY_CREATE,
                                                    StandardWatchEventKinds.ENTRY_DELETE,
                                                    StandardWatchEventKinds.ENTRY_MODIFY));
                                            final EDirectoryCreated directoryCreatedEvent = new EDirectoryCreated();
                                            directoryCreatedEvent.setLocalAbsolutePath(dir.toString());
                                            directoryCreatedEvent.setLocalRelativePath(watchPath.relativize(dir).toString());
                                            directoryCreatedEvent.setLocalRoot(watchPath.toString());
                                            directoryCreatedBus.fireAsync(directoryCreatedEvent);
                                            return FileVisitResult.CONTINUE;
                                        }

                                        @Override
                                        public FileVisitResult visitFile(final Path file,
                                                                         final BasicFileAttributes attrs) throws IOException
                                        {
                                            final EFileCreated fileCreatedEvent = new EFileCreated();
                                            fileCreatedEvent.setLocalAbsolutePath(file.toString());
                                            fileCreatedEvent.setLocalRelativePath(watchPath.relativize(file).toString());
                                            fileCreatedEvent.setLocalRoot(watchPath.toString());
                                            fileCreatedBus.fireAsync(fileCreatedEvent);
                                            return FileVisitResult.CONTINUE;
                                        }
                                    });
                                }
                                catch (IOException e)
                                {
                                    e.printStackTrace();
                                }
                            }
                            else
                            {
                                final EFileCreated fileCreatedEvent = new EFileCreated();
                                fileCreatedEvent.setLocalAbsolutePath(absoluteLocalFilePath.toString());
                                fileCreatedEvent.setLocalRelativePath(relativeLocalFilePath.toString());
                                fileCreatedEvent.setLocalRoot(watchPath.toString());
                                fileCreatedBus.fireAsync(fileCreatedEvent);
                            }
                        }
                        else if (event.kind().equals(StandardWatchEventKinds.ENTRY_DELETE))
                        {

                        }
                        else if (event.kind().equals(StandardWatchEventKinds.ENTRY_MODIFY))
                        {

                        }
                    }
                }
            }

            try
            {
                Thread.sleep(QUANTUM);
            }
            catch (InterruptedException e)
            {
                break;
            }
        }
    }
}
