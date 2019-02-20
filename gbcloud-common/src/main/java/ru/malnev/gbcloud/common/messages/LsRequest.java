package ru.malnev.gbcloud.common.messages;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
public class LsRequest extends AbstractMessage
{
    @Getter
    @Setter
    private String requestedPath;
}
