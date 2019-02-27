package ru.malnev.gbcloud.common.events;

import lombok.Getter;
import lombok.Setter;
import ru.malnev.gbcloud.common.conversations.IConversation;

public class EConversationFailed extends EConversationEvent
{
    @Getter
    @Setter
    private String reason;

    @Getter
    @Setter
    private boolean remote;

    public EConversationFailed(final IConversation conversation)
    {
        super(conversation);
    }

    public EConversationFailed(final IConversation conversation,
                               final String reason)
    {
        super(conversation);
        this.reason = reason;
    }
}
