package ru.malnev.gbcloud.common.filesystem;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.Path;

public interface IFile extends AutoCloseable
{
    void open(@NotNull Path filePath) throws IOException;

    long getSize() throws IOException;
}
