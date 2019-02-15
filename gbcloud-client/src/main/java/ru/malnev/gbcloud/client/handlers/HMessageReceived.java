package ru.malnev.gbcloud.client.handlers;

import ru.malnev.gbcloud.client.events.EMessageReceived;
import ru.malnev.gbcloud.common.conversations.IConversationManager;
import ru.malnev.gbcloud.common.messages.IMessage;
import ru.malnev.gbcloud.common.transport.ITransportChannel;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.ObservesAsync;

@ApplicationScoped
public class HMessageReceived
{
    private void handle(@ObservesAsync final EMessageReceived event)
    {
        final IConversationManager conversationManager = event.getConversationManager();
        final IMessage message = event.getMessage();
        final ITransportChannel transportChannel = event.getTransportChannel();
        conversationManager.dispatchMessage(message, transportChannel);
    }
}
