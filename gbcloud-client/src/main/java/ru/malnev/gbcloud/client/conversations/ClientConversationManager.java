package ru.malnev.gbcloud.client.conversations;

import org.jetbrains.annotations.NotNull;
import ru.malnev.gbcloud.common.conversations.AbstractConversationManager;

public class ClientConversationManager extends AbstractConversationManager
{
    public void startConversation(final @NotNull IClientAgent conversation)
    {
        conversation.setConversationManager(this);
        conversationMap.put(conversation.getId(), conversation);
        conversation.start();
    }
}
