package ru.malnev.gbcloud.common.filesystem;

import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Set;

public abstract class NioFile implements IFile
{
    @Getter
    private Path filePath;

    protected SeekableByteChannel byteChannel;

    @Override
    public void open(@NotNull Path filePath) throws IOException
    {
        byteChannel = Files.newByteChannel(filePath, getOpenOptions());
        this.filePath = filePath;
    }

    @Override
    public void close() throws Exception
    {
        byteChannel.close();
    }

    protected abstract Set<StandardOpenOption> getOpenOptions();
}
