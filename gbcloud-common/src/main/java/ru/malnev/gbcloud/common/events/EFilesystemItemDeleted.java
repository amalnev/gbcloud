package ru.malnev.gbcloud.common.events;

public class EFilesystemItemDeleted extends EFilesystemEvent
{
    public EFilesystemItemDeleted(final String localAbsolutePath,
                                  final String localRelativePath,
                                  final String localRoot)
    {
        super(localAbsolutePath, localRelativePath, localRoot);
    }
}
