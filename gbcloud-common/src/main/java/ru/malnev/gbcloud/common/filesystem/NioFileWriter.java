package ru.malnev.gbcloud.common.filesystem;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.StandardOpenOption;
import java.util.HashSet;
import java.util.Set;

public class NioFileWriter extends NioFile implements IFileWriter
{
    @Override
    public void write(@NotNull byte[] bytesToWrite) throws IOException
    {
        final ByteBuffer outputBuffer = ByteBuffer.wrap(bytesToWrite);
        byteChannel.write(outputBuffer);
    }

    @Override
    protected Set<StandardOpenOption> getOpenOptions()
    {
        final Set<StandardOpenOption> options = new HashSet<>();
        options.add(StandardOpenOption.WRITE);
        options.add(StandardOpenOption.CREATE);
        return options;
    }
}
