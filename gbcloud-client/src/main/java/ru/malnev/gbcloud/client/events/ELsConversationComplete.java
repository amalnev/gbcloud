package ru.malnev.gbcloud.client.events;

import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import ru.malnev.gbcloud.common.conversations.IConversation;
import ru.malnev.gbcloud.common.messages.LsResponse;

public class ELsConversationComplete extends EConversationComplete
{
    @Getter
    private LsResponse response;

    public ELsConversationComplete(final @NotNull IConversation conversation,
                                   final @NotNull LsResponse response)
    {
        super(conversation);
    }
}
