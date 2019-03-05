package ru.malnev.gbcloud.common.events;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class EDirectoryCreated extends EFilesystemEvent
{
    public EDirectoryCreated(final String localAbsolutePath,
                             final String localRelativePath,
                             final String localRoot)
    {
        super(localAbsolutePath, localRelativePath, localRoot);
    }
}
