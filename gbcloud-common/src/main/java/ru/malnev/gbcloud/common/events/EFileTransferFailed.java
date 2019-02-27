package ru.malnev.gbcloud.common.events;

import lombok.Getter;
import lombok.Setter;
import ru.malnev.gbcloud.common.conversations.IConversation;

public class EFileTransferFailed extends EConversationFailed
{
    @Getter
    @Setter
    private String fileName;

    public EFileTransferFailed(IConversation conversation)
    {
        super(conversation);
    }
}
