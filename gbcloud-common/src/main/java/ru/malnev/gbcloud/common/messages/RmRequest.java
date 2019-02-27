package ru.malnev.gbcloud.common.messages;

import lombok.Getter;
import lombok.Setter;

public class RmRequest extends AbstractMessage
{
    @Getter
    @Setter
    private String targetPath;
}
