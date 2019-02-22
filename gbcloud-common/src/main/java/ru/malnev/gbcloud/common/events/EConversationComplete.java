package ru.malnev.gbcloud.common.events;

import ru.malnev.gbcloud.common.conversations.IConversation;

public class EConversationComplete extends EConversationEvent
{
    public EConversationComplete(IConversation conversation)
    {
        super(conversation);
    }
}
