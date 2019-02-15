package ru.malnev.gbcloud.common.messages;

import org.jetbrains.annotations.NotNull;

import java.io.Serializable;

public interface IMessage extends Serializable
{
    @NotNull
    String getId();

    @NotNull
    String getConversationId();

    void setConversationId(@NotNull String conversationId);

    @NotNull
    MessageType getType();

    void setType(@NotNull MessageType type);
}
