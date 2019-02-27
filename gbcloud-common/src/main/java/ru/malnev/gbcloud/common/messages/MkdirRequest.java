package ru.malnev.gbcloud.common.messages;

import lombok.Getter;
import lombok.Setter;

public class MkdirRequest extends AbstractMessage
{
    @Getter
    @Setter
    private String directoryPath;
}
