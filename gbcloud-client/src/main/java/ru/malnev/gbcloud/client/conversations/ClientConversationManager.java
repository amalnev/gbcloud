package ru.malnev.gbcloud.client.conversations;

import ru.malnev.gbcloud.common.conversations.AbstractConversationManager;
import ru.malnev.gbcloud.common.conversations.IConversation;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.spi.CDI;

@ApplicationScoped
public class ClientConversationManager extends AbstractConversationManager
{
    public <T extends IConversation> void startConversation(Class<T> conversationClass)
    {
        startConversation(CDI.current().select(conversationClass).get());
    }

    public void authenticate()
    {
        startConversation(AuthenticationClientAgent.class);
    }
}
