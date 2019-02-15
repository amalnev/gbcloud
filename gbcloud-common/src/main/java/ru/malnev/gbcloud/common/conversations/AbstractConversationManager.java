package ru.malnev.gbcloud.common.conversations;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.malnev.gbcloud.common.messages.IMessage;
import ru.malnev.gbcloud.common.messages.UnexpectedMessageResponse;
import ru.malnev.gbcloud.common.transport.ITransportChannel;

import java.util.HashMap;
import java.util.Map;

public abstract class AbstractConversationManager implements IConversationManager
{
    protected Map<String, IConversation> conversationMap = new HashMap<>();

    @Override
    public void dispatchMessage(final @NotNull IMessage message,
                                final @NotNull ITransportChannel transportChannel)
    {
        final IConversation targetConversation = conversationMap.get(message.getConversationId());
        if (targetConversation != null)
        {
            targetConversation.processMessage(message, transportChannel);
        }
        else
        {
            final IConversation newConversation = initiateConversation(message);
            if (newConversation == null)
            {
                final IMessage unexpectedMessageResponse = new UnexpectedMessageResponse();
                unexpectedMessageResponse.setConversationId(message.getConversationId());
                transportChannel.sendMessage(unexpectedMessageResponse);
                return;
            }
            newConversation.setConversationManager(this);
            conversationMap.put(newConversation.getId(), newConversation);
            newConversation.processMessage(message, transportChannel);
        }
    }

    @Override
    public void stopConversation(@NotNull IConversation conversation)
    {
        conversationMap.remove(conversation.getId());
    }

    @Nullable
    protected IConversation initiateConversation(final @NotNull IMessage message)
    {
        return null;
    }
}
