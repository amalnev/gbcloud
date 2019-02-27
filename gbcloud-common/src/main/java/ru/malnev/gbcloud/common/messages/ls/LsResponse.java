package ru.malnev.gbcloud.common.messages.ls;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.SneakyThrows;
import ru.malnev.gbcloud.common.messages.AbstractMessage;

import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;

public class LsResponse extends AbstractMessage
{
    @Getter
    @Setter
    @NoArgsConstructor
    public static class FilesystemElement implements Serializable
    {
        private String name;

        private boolean isDirectory;

        private long size;

        @SneakyThrows
        public FilesystemElement(final Path targetPath)
        {
            setName(targetPath.getName(targetPath.getNameCount() - 1).toString());
            if (Files.isDirectory(targetPath))
            {
                setDirectory(true);
            }
            else
            {
                setDirectory(false);
                setSize(Files.size(targetPath));
            }
        }
    }

    @Getter
    private final List<FilesystemElement> elements = new LinkedList<>();
}
