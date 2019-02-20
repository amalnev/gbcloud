package ru.malnev.gbcloud.common.messages;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.SneakyThrows;
import ru.malnev.gbcloud.common.utils.Util;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.Instant;
import java.util.LinkedList;
import java.util.List;

public class LsResponse extends AbstractMessage
{
    @Getter
    @Setter
    @NoArgsConstructor
    public static class FilesystemElement
    {
        private String name;

        private boolean isDirectory;

        private Instant timeCreated;

        private Instant timeModified;

        private long checkSum;

        private long size;

        @SneakyThrows
        public FilesystemElement(final Path targetPath)
        {
            final BasicFileAttributes attributes = Files.readAttributes(targetPath, BasicFileAttributes.class);
            setName(targetPath.getName(targetPath.getNameCount() - 1).toString());
            setTimeCreated(attributes.creationTime().toInstant());
            setTimeModified(attributes.lastModifiedTime().toInstant());
            if (Files.isDirectory(targetPath))
            {
                setDirectory(true);
            }
            else
            {
                setDirectory(false);
                setSize(Files.size(targetPath));
                setCheckSum(Util.calculateCheckSum(targetPath.toString()));
            }
        }
    }

    @Getter
    @Setter
    private boolean existing;

    @Getter
    @Setter
    private String requestedPath;

    @Getter
    private final List<FilesystemElement> elements = new LinkedList<>();
}
