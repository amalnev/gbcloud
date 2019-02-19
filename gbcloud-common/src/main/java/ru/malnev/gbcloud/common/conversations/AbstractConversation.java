package ru.malnev.gbcloud.common.conversations;

import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;
import ru.malnev.gbcloud.common.messages.IMessage;
import ru.malnev.gbcloud.common.messages.UnauthorizedResponse;
import ru.malnev.gbcloud.common.messages.UnexpectedMessageResponse;
import ru.malnev.gbcloud.common.transport.ITransportChannel;

import javax.interceptor.AroundInvoke;
import javax.interceptor.InvocationContext;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;

public abstract class AbstractConversation implements IConversation
{
    private static final long DEFAULT_TIMEOUT = 200000;

    @Getter
    @Setter
    private volatile String id = UUID.randomUUID().toString();

    @Getter
    @Setter
    private volatile IConversationManager conversationManager;

    private volatile long lastActivityTime = 0;

    @Getter
    @Setter
    private volatile long timeoutMillis = DEFAULT_TIMEOUT;

    private final Thread timeoutWorker = new Thread(() ->
    {
        while(true)
        {
            try
            {
                Thread.sleep(timeoutMillis);
                final long delta = System.currentTimeMillis() - lastActivityTime;
                if(delta >= timeoutMillis)
                {
                    getConversationManager().stopConversation(this);
                    break;
                }
            }
            catch (InterruptedException e)
            {
                break;
            }
        }
    });

    private Set<Class<? extends IMessage>> expectedMessages = new LinkedHashSet<>();

    protected synchronized void expectMessage(final @NotNull Class<? extends IMessage> messageClass)
    {
        expectedMessages.add(messageClass);
    }

    protected synchronized void stopExpectingMessage(final @NotNull Class<? extends IMessage> messageClass)
    {
        expectedMessages.remove(messageClass);
    }

    @Override
    public void sendMessageToPeer(final @NotNull IMessage message)
    {
        message.setConversationId(getId());
        getConversationManager().getTransportChannel().sendMessage(message);
    }

    @AroundInvoke
    @SneakyThrows
    private Object wrapMethods(final InvocationContext invocationContext)
    {
        final String invokedMethodName = invocationContext.getMethod().getName();
        if (invokedMethodName.equals("processMessageFromPeer"))
        {
            lastActivityTime = System.currentTimeMillis();
            final Class messageClass = invocationContext.getParameters()[0].getClass();
            if (expectedMessages.contains(messageClass))
            {
                expectedMessages.remove(messageClass);
            }
            else if(messageClass.equals(UnexpectedMessageResponse.class) ||
                    messageClass.equals(UnauthorizedResponse.class))
            {
                conversationManager.stopConversation(this);
                return null;
            }
            else
            {
                sendMessageToPeer(new UnexpectedMessageResponse());
                return null;
            }
        }
        else if(invokedMethodName.equals("start"))
        {
            lastActivityTime = System.currentTimeMillis();
            timeoutWorker.start();
        }

        try
        {
            return invocationContext.proceed();
        }
        finally
        {
            if(invokedMethodName.equals("stop"))
            {
                timeoutWorker.interrupt();
            }
        }
    }
}
