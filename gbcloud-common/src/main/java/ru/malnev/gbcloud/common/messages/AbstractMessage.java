package ru.malnev.gbcloud.common.messages;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

public abstract class AbstractMessage implements IMessage
{
    @Getter
    private String id = UUID.randomUUID().toString();

    @Getter
    @Setter
    private String conversationId;
}
