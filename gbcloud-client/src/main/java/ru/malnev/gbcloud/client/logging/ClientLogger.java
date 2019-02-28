package ru.malnev.gbcloud.client.logging;

import lombok.SneakyThrows;
import ru.malnev.gbcloud.client.events.EAuthFailure;
import ru.malnev.gbcloud.client.events.EMessageReceived;
import ru.malnev.gbcloud.client.handlers.*;
import ru.malnev.gbcloud.common.conversations.IConversationManager;
import ru.malnev.gbcloud.common.events.EConversationComplete;
import ru.malnev.gbcloud.common.events.EConversationFailed;
import ru.malnev.gbcloud.common.events.EConversationTimedOut;
import ru.malnev.gbcloud.common.logging.CommonLogger;
import ru.malnev.gbcloud.common.messages.IMessage;
import ru.malnev.gbcloud.common.transport.ITransportChannel;

import javax.inject.Inject;
import javax.interceptor.AroundInvoke;
import javax.interceptor.InvocationContext;

public class ClientLogger extends CommonLogger
{
    @Inject
    private IConversationManager conversationManager;

    @AroundInvoke
    @SneakyThrows
    private Object logMethod(final InvocationContext invocationContext)
    {
        final Class targetClass = invocationContext.getTarget().getClass();

        if (HMessageReceived.class.isAssignableFrom(targetClass))
        {
            final EMessageReceived event = (EMessageReceived) invocationContext.getParameters()[0];
            final IMessage message = event.getMessage();
            final ITransportChannel transportChannel = conversationManager.getTransportChannel();
            String remoteAddress = null;
            if (transportChannel.isConnected())
            {
                remoteAddress = transportChannel.getRemoteAddress();
            }

            write("Message of type " +
                    message.getClass().getSimpleName() +
                    " bound to conversation " +
                    message.getConversationId() +
                    " was received from " +
                    remoteAddress);
        }
        else if (HAuthSuccess.class.isAssignableFrom(targetClass))
        {
            write("Authentication success.");
        }
        else if (HAuthFailure.class.isAssignableFrom(targetClass))
        {
            final EAuthFailure event = (EAuthFailure) invocationContext.getParameters()[0];
            write("Authentication failure. Reason: " + event.getReason());
        }
        else if (HConversationFailed.class.isAssignableFrom(targetClass))
        {
            final EConversationFailed event = (EConversationFailed) invocationContext.getParameters()[0];
            write("Conversation " + event.getConversation().getId() + " failed. Reason: " +
                    event.getReason() + ". Remote: " + event.isRemote());
        }
        else if (HConversationTimedOut.class.isAssignableFrom(targetClass))
        {
            final EConversationTimedOut event = (EConversationTimedOut) invocationContext.getParameters()[0];
            write("Conversation " + event.getConversation().getId() + " timed out.");
        }
        else if (HConversationComplete.class.isAssignableFrom(targetClass))
        {
            final EConversationComplete event = (EConversationComplete) invocationContext.getParameters()[0];
            write("Conversation " + event.getConversation().getId() + " completed.");
        }
        else if (HConnectionClosed.class.isAssignableFrom(targetClass))
        {
            write("Disconnected.");
        }

        return invocationContext.proceed();
    }
}
