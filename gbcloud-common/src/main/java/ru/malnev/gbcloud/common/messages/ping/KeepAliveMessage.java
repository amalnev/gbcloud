package ru.malnev.gbcloud.common.messages.ping;

import ru.malnev.gbcloud.common.messages.AbstractMessage;

public class KeepAliveMessage extends AbstractMessage
{
    byte[] buffer = new byte[1024 * 1024];
}
