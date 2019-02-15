package ru.malnev.gbcloud.common.messages;

public class KeepAliveMessage extends AbstractMessage
{
    public KeepAliveMessage()
    {
        setType(MessageType.KEEP_ALIVE);
    }
}
