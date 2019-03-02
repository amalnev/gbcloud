package ru.malnev.gbcloud.common.events;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class EFilesystemEvent
{
    private String localAbsolutePath;

    private String localRelativePath;

    private String localRoot;
}
