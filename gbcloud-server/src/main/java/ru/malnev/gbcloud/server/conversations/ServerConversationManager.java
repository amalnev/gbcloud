package ru.malnev.gbcloud.server.conversations;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.malnev.gbcloud.common.conversations.AbstractConversationManager;
import ru.malnev.gbcloud.common.conversations.IConversation;
import ru.malnev.gbcloud.common.messages.IMessage;
import ru.malnev.gbcloud.common.messages.KeepAliveMessage;

import javax.enterprise.inject.spi.CDI;

public class ServerConversationManager extends AbstractConversationManager
{
    @Nullable
    @Override
    protected IConversation initiateConversation(final @NotNull IMessage message)
    {
        IConversation conversation = null;
        if (message instanceof KeepAliveMessage)
        {
            conversation = CDI.current().select(KeepAliveServerAgent.class).get();
            conversation.setId(message.getConversationId());
        }
        return conversation;
    }
}
