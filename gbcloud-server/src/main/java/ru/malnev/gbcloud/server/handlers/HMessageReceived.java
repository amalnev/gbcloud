package ru.malnev.gbcloud.server.handlers;

import ru.malnev.gbcloud.common.conversations.IConversationManager;
import ru.malnev.gbcloud.common.messages.auth.AuthMessage;
import ru.malnev.gbcloud.common.messages.auth.UnauthorizedResponse;
import ru.malnev.gbcloud.common.transport.ITransportChannel;
import ru.malnev.gbcloud.server.conversations.ServerConversationManager;
import ru.malnev.gbcloud.server.events.EMessageReceived;
import ru.malnev.gbcloud.server.logging.ServerLogger;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.ObservesAsync;
import javax.interceptor.Interceptors;

@ApplicationScoped
@Interceptors(ServerLogger.class)
public class HMessageReceived
{
    private void handleMessageReceived(@ObservesAsync final EMessageReceived event)
    {
        final ServerConversationManager conversationManager = event.getConversationManager();
        final ITransportChannel transportChannel = conversationManager.getTransportChannel();
        if (conversationManager.isAuthenticated() || event.getMessage() instanceof AuthMessage)
        {
            conversationManager.dispatchMessage(event.getMessage());
        }
        else
        {
            final UnauthorizedResponse unauthorizedResponse = new UnauthorizedResponse();
            unauthorizedResponse.setConversationId(event.getMessage().getConversationId());
            try
            {
                transportChannel.sendMessage(new UnauthorizedResponse());
            }
            catch (Exception e)
            {
                transportChannel.closeSilently();
            }
        }
    }
}
