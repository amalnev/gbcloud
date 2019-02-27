package ru.malnev.gbcloud.common.messages;

import lombok.Getter;
import lombok.Setter;

public class FileDataAcceptedResponse extends AbstractMessage
{
    @Getter
    @Setter
    private int cseq;
}
