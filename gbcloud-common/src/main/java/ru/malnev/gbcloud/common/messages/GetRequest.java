package ru.malnev.gbcloud.common.messages;

import lombok.Getter;
import lombok.Setter;

public class GetRequest extends AbstractMessage
{
    @Getter
    @Setter
    private String fileName;
}
