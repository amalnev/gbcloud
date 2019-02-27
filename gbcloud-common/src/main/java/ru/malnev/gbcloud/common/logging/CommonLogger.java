package ru.malnev.gbcloud.common.logging;

import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;
import ru.malnev.gbcloud.common.messages.IMessage;
import ru.malnev.gbcloud.common.transport.ITransportChannel;

import javax.interceptor.AroundInvoke;
import javax.interceptor.InvocationContext;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.logging.Logger;

public class CommonLogger
{
    private static final String DATE_FORMAT_NOW = "[dd.MM.yyyy HH:mm:ss]";

    private Logger logger = Logger.getGlobal();

    @NotNull
    private String now()
    {
        final Calendar cal = Calendar.getInstance();
        final SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_NOW);
        return sdf.format(cal.getTime());
    }

    protected void write(final @NotNull String line)
    {
        logger.info(now() + " " + line);
        //System.out.println(now() + " " + line);
    }

    @AroundInvoke
    @SneakyThrows
    private Object logMethod(final InvocationContext invocationContext)
    {
        final Class targetClass = invocationContext.getTarget().getClass();
        if (ITransportChannel.class.isAssignableFrom(targetClass))
        {
            final ITransportChannel transportChannel = (ITransportChannel) invocationContext.getTarget();
            String remoteAddress = "[unknown]";
            if (transportChannel.isConnected())
            {
                remoteAddress = transportChannel.getRemoteAddress();
            }
            final Method method = invocationContext.getMethod();
            if (method.getName().equals("sendMessage"))
            {
                final IMessage message = (IMessage) invocationContext.getParameters()[0];
                final String messageType = message.getClass().getSimpleName();
                final String conversationId = message.getConversationId();
                write("Message of type " +
                        messageType +
                        " with destination " +
                        remoteAddress +
                        " bound to conversation " +
                        conversationId +
                        " is about to be sent.");
            }
            else if (method.getName().equals("close"))
            {
                write("Connection to " + remoteAddress + " is about to close.");
            }
        }
        return invocationContext.proceed();
    }
}
