package ru.malnev.gbcloud.common.events;

import ru.malnev.gbcloud.common.conversations.IConversation;

public class EConversationFailed extends EConversationEvent
{
    public EConversationFailed(IConversation conversation)
    {
        super(conversation);
    }
}
