package ru.malnev.gbcloud.common.events;

import ru.malnev.gbcloud.common.conversations.IConversation;

public class EConversationTimedOut extends EConversationFailed
{
    public EConversationTimedOut(final IConversation conversation)
    {
        super(conversation);
    }
}
