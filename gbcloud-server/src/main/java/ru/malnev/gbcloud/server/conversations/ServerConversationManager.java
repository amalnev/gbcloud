package ru.malnev.gbcloud.server.conversations;

import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.malnev.gbcloud.common.conversations.AbstractConversationManager;
import ru.malnev.gbcloud.common.conversations.IConversation;
import ru.malnev.gbcloud.common.messages.AuthMessage;
import ru.malnev.gbcloud.common.messages.IMessage;
import ru.malnev.gbcloud.common.messages.KeepAliveMessage;
import ru.malnev.gbcloud.server.persistence.entitites.User;

import javax.enterprise.inject.spi.CDI;

public class ServerConversationManager extends AbstractConversationManager
{
    @Getter
    @Setter
    private User user;

    public boolean isAuthenticated()
    {
        return user != null;
    }

    @Nullable
    @Override
    protected IConversation initiateConversation(final @NotNull IMessage message)
    {
        IConversation conversation = null;
        if (message instanceof KeepAliveMessage)
        {
            conversation = CDI.current().select(KeepAliveServerAgent.class).get();
        }
        else if (message instanceof AuthMessage)
        {
            conversation = CDI.current().select(AuthenticationServerAgent.class).get();
        }
        if (conversation != null) conversation.setId(message.getConversationId());
        return conversation;
    }
}
