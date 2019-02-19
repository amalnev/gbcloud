package ru.malnev.gbcloud.server.logging;

import lombok.SneakyThrows;
import ru.malnev.gbcloud.common.logging.CommonLogger;
import ru.malnev.gbcloud.server.events.EClientConntected;
import ru.malnev.gbcloud.server.events.EMessageReceived;

import javax.interceptor.AroundInvoke;
import javax.interceptor.InvocationContext;
import java.lang.reflect.Method;

public class ServerLogger extends CommonLogger
{
    @AroundInvoke
    @SneakyThrows
    private Object logMethod(final InvocationContext invocationContext)
    {
        final Method method = invocationContext.getMethod();
        if (method.getName().equals("handleClientConnected"))
        {
            final EClientConntected event = (EClientConntected) invocationContext.getParameters()[0];
            final String remoteAddress = event.getClientContext()
                    .getConversationManager()
                    .getTransportChannel()
                    .getRemoteAddress();
            write("Connection from " + remoteAddress + " accepted.");
        }
        else if (method.getName().equals("handleMessageReceived"))
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
        return invocationContext.proceed();
    }
}
