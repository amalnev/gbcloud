package ru.malnev.gbcloud.client.handlers;

import ru.malnev.gbcloud.client.conversations.ClientConversationManager;
import ru.malnev.gbcloud.client.events.EMessageReceived;
import ru.malnev.gbcloud.client.logging.ClientLogger;
import ru.malnev.gbcloud.common.conversations.IConversationManager;
import ru.malnev.gbcloud.common.messages.IMessage;
import ru.malnev.gbcloud.common.transport.ITransportChannel;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.ObservesAsync;
import javax.inject.Inject;
import javax.interceptor.Interceptors;

@ApplicationScoped
@Interceptors(ClientLogger.class)
public class HMessageReceived
{
    @Inject
    private IConversationManager conversationManager;

    private void handleMessageReceived(@ObservesAsync final EMessageReceived event)
    {
        conversationManager.dispatchMessage(event.getMessage());
    }
}
