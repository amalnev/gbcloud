package ru.malnev.gbcloud.common.filesystem;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public interface IFileWriter extends IFile
{
    void write(@NotNull byte[] bytesToWrite) throws IOException;
}
