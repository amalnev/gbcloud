package ru.malnev.gbcloud.common.filesystem;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.Path;

public interface IFileReader extends IFile
{
    byte[] read(int bytesToRead) throws IOException;
}
