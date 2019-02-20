package ru.malnev.gbcloud.client.events;

import ru.malnev.gbcloud.common.conversations.IConversation;
import ru.malnev.gbcloud.common.events.EConversationEvent;

public abstract class EConversationComplete extends EConversationEvent
{
    public EConversationComplete(final IConversation conversation)
    {
        super(conversation);
    }
}
