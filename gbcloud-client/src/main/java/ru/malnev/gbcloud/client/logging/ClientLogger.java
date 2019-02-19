package ru.malnev.gbcloud.client.logging;

import lombok.SneakyThrows;
import ru.malnev.gbcloud.client.events.EAuthFailure;
import ru.malnev.gbcloud.client.events.EMessageReceived;
import ru.malnev.gbcloud.common.conversations.IConversationManager;
import ru.malnev.gbcloud.common.logging.CommonLogger;
import ru.malnev.gbcloud.common.messages.IMessage;
import ru.malnev.gbcloud.common.transport.ITransportChannel;

import javax.inject.Inject;
import javax.interceptor.AroundInvoke;
import javax.interceptor.InvocationContext;
import java.lang.reflect.Method;

public class ClientLogger extends CommonLogger
{
    @Inject
    private IConversationManager conversationManager;

    @AroundInvoke
    @SneakyThrows
    private Object logMethod(final InvocationContext invocationContext)
    {
        final Method method = invocationContext.getMethod();
        if (method.getName().equals("handleMessageReceived"))
        {
            final EMessageReceived event = (EMessageReceived) invocationContext.getParameters()[0];
            final IMessage message = event.getMessage();
            final ITransportChannel transportChannel = conversationManager.getTransportChannel();
            String remoteAddress = null;
            if(transportChannel.isConnected())
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
        else if(method.getName().equals("handleAuthSuccess"))
        {
            write("Authentication success.");
        }
        else if(method.getName().equals("handleAuthFailure"))
        {
            final EAuthFailure event = (EAuthFailure) invocationContext.getParameters()[1];
            write("Authentication failure. Reason: ");
        }

        return invocationContext.proceed();
    }
}
