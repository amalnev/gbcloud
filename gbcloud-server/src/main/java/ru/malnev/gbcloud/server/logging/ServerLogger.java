package ru.malnev.gbcloud.server.logging;

import lombok.SneakyThrows;
import ru.malnev.gbcloud.common.events.EConversationFailed;
import ru.malnev.gbcloud.common.events.EConversationTimedOut;
import ru.malnev.gbcloud.common.logging.CommonLogger;
import ru.malnev.gbcloud.server.events.EClientConntected;
import ru.malnev.gbcloud.server.events.EMessageReceived;
import ru.malnev.gbcloud.server.handlers.HClientConnected;
import ru.malnev.gbcloud.server.handlers.HConversationFailed;
import ru.malnev.gbcloud.server.handlers.HConversationTimedOut;
import ru.malnev.gbcloud.server.handlers.HMessageReceived;

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
        //final Method method = invocationContext.getMethod();
        if (HClientConnected.class.isAssignableFrom(targetClass)/*method.getName().equals("handleClientConnected")*/)
        {
            final EClientConntected event = (EClientConntected) invocationContext.getParameters()[0];
            final String remoteAddress = event.getClientContext()
                    .getConversationManager()
                    .getTransportChannel()
                    .getRemoteAddress();
            write("Connection from " + remoteAddress + " accepted.");
        }
        else if (HMessageReceived.class.isAssignableFrom(targetClass)/*method.getName().equals("handleMessageReceived")*/)
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
            write("Conversation " + event.getConversation().getId() + " failed.");
        }
        else if(HConversationTimedOut.class.isAssignableFrom(targetClass))
        {
            final EConversationTimedOut event = (EConversationTimedOut) invocationContext.getParameters()[0];
            write("Conversation " + event.getConversation().getId() + " timed out.");
        }
        return invocationContext.proceed();
    }
}
