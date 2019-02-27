package ru.malnev.gbcloud.common.messages.cd;

import lombok.Getter;
import lombok.Setter;
import ru.malnev.gbcloud.common.messages.AbstractMessage;

public class CdRequest extends AbstractMessage
{
    @Getter
    @Setter
    private String targetDirectory;
}
