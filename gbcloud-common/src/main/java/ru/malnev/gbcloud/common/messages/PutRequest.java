package ru.malnev.gbcloud.common.messages;

import lombok.Getter;
import lombok.Setter;

public class PutRequest extends AbstractMessage
{
    @Getter
    @Setter
    private String fileName;
}
