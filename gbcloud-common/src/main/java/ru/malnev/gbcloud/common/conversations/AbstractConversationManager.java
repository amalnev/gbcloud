package ru.malnev.gbcloud.common.conversations;

import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.malnev.gbcloud.common.messages.IMessage;
import ru.malnev.gbcloud.common.messages.UnexpectedMessageResponse;
import ru.malnev.gbcloud.common.transport.ITransportChannel;

import java.util.HashMap;
import java.util.Map;

public abstract class AbstractConversationManager implements IConversationManager
{
    @Getter
    @Setter
    private ITransportChannel transportChannel;

    private Map<String, IConversation> conversationMap = new HashMap<>();

    @Override
    public void dispatchMessage(final @NotNull IMessage message)
    {
        IConversation targetConversation = null;
        synchronized (this)
        {
            targetConversation = conversationMap.get(message.getConversationId());
        }

        if (targetConversation != null)
        {
            targetConversation.processMessageFromPeer(message);
        }
        else
        {
            if (message.getConversationId() == null) return;
            if (message instanceof UnexpectedMessageResponse) return;
            final IConversation newConversation = initiateConversation(message);
            if (newConversation == null)
            {
                final IMessage unexpectedMessageResponse = new UnexpectedMessageResponse();
                unexpectedMessageResponse.setConversationId(message.getConversationId());
                transportChannel.sendMessage(unexpectedMessageResponse);
                return;
            }
            newConversation.setConversationManager(this);
            synchronized (this)
            {
                conversationMap.put(newConversation.getId(), newConversation);
            }
            newConversation.start();
            newConversation.processMessageFromPeer(message);
        }
    }

    @Override
    public void stopConversation(@NotNull IConversation conversation)
    {
        conversation.stop();
        synchronized (this)
        {
            conversationMap.remove(conversation.getId());
        }
    }

    @Override
    public void startConversation(@NotNull IConversation conversation)
    {
        synchronized (this)
        {
            conversationMap.put(conversation.getId(), conversation);
        }
        conversation.setConversationManager(this);
        conversation.start();
    }

    @Nullable
    protected synchronized IConversation initiateConversation(final @NotNull IMessage message)
    {
        return null;
    }
}
