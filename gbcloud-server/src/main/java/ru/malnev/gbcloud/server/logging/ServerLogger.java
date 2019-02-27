package ru.malnev.gbcloud.server.logging;

import lombok.SneakyThrows;
import ru.malnev.gbcloud.common.events.EConversationComplete;
import ru.malnev.gbcloud.common.events.EConversationFailed;
import ru.malnev.gbcloud.common.events.EConversationTimedOut;
import ru.malnev.gbcloud.common.logging.CommonLogger;
import ru.malnev.gbcloud.server.events.EClientConntected;
import ru.malnev.gbcloud.server.events.EMessageReceived;
import ru.malnev.gbcloud.server.handlers.*;

import javax.interceptor.AroundInvoke;
import javax.interceptor.InvocationContext;
import java.lang.reflect.Method;

public class ServerLogger extends CommonLogger
{
    @AroundInvoke
    @SneakyThrows
    private Object logMethod(final InvocationContext invocationContext)
    {
        final Class targetClass = invocationContext.getTarget().getClass();
        if (HClientConnected.class.isAssignableFrom(targetClass))
        {
            final EClientConntected event = (EClientConntected) invocationContext.getParameters()[0];
            final String remoteAddress = event.getClientContext()
                    .getConversationManager()
                    .getTransportChannel()
                    .getRemoteAddress();
            write("Connection from " + remoteAddress + " accepted.");
        }
        else if (HMessageReceived.class.isAssignableFrom(targetClass))
        {
            final EMessageReceived event = (EMessageReceived) invocationContext.getParameters()[0];
            final String remoteAddress = event.getClientContext()
                    .getConversationManager()
                    .getTransportChannel()
                    .getRemoteAddress();
            final String messageType = event.getMessage().getClass().getSimpleName();
            final String conversationId = event.getMessage().getConversationId();
            write("A message of type " +
                    messageType +
                    " bound to conversation " +
                    conversationId +
                    " was received from " +
                    remoteAddress);
        }
        else if(HConversationFailed.class.isAssignableFrom(targetClass))
        {
            final EConversationFailed event = (EConversationFailed) invocationContext.getParameters()[0];
            write("Conversation " + event.getConversation().getId() + " failed. Reason: " +
                    event.getReason() + ". Remote: " + event.isRemote());
        }
        else if(HConversationTimedOut.class.isAssignableFrom(targetClass))
        {
            final EConversationTimedOut event = (EConversationTimedOut) invocationContext.getParameters()[0];
            write("Conversation " + event.getConversation().getId() + " timed out.");
        }
        else if(HConversationComplete.class.isAssignableFrom(targetClass))
        {
            final EConversationComplete event = (EConversationComplete) invocationContext.getParameters()[0];
            write("Conversation " + event.getConversation().getId() + " completed.");
        }
        return invocationContext.proceed();
    }
}
