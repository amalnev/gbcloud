package ru.malnev.gbcloud.common.messages;

public class KeepAliveMessage extends AbstractMessage
{
    byte[] buffer = new byte[1024 * 1024];
}
