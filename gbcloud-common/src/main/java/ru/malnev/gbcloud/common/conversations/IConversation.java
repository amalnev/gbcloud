package ru.malnev.gbcloud.common.conversations;

import org.jetbrains.annotations.NotNull;
import ru.malnev.gbcloud.common.messages.IMessage;
import ru.malnev.gbcloud.common.transport.ITransportChannel;

public interface IConversation
{
    @NotNull
    String getId();

    void setId(@NotNull String id);

    @NotNull
    IConversationManager getConversationManager();

    void setConversationManager(@NotNull IConversationManager conversationManager);

    void processMessageFromPeer(@NotNull IMessage message);

    void sendMessageToPeer(@NotNull IMessage message);

    default void start() {}

    default void stop() {}

    long getTimeoutMillis();

    void setTimeoutMillis(long timeoutMillis);
}
