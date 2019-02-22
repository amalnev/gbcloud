package ru.malnev.gbcloud.common.messages;

import lombok.Getter;
import lombok.Setter;

public class CdRequest extends AbstractMessage
{
    @Getter
    @Setter
    private String targetDirectory;
}
