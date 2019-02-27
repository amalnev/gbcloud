package ru.malnev.gbcloud.common.messages.transfer;

import lombok.Getter;
import lombok.Setter;
import ru.malnev.gbcloud.common.messages.AbstractMessage;

public class PutRequest extends AbstractMessage
{
    @Getter
    @Setter
    private String fileName;
}
