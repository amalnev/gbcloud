package ru.malnev.gbcloud.common.events;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class EFileCreated extends EFilesystemEvent
{
    public EFileCreated(final String localAbsolutePath,
                        final String localRelativePath,
                        final String localRoot)
    {
        super(localAbsolutePath, localRelativePath, localRoot);
    }
}
