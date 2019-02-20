package ru.malnev.gbcloud.common.conversations;

import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;
import ru.malnev.gbcloud.common.events.EConversationFailed;
import ru.malnev.gbcloud.common.events.EConversationTimedOut;
import ru.malnev.gbcloud.common.messages.IMessage;
import ru.malnev.gbcloud.common.messages.ServerErrorResponse;
import ru.malnev.gbcloud.common.messages.UnauthorizedResponse;
import ru.malnev.gbcloud.common.messages.UnexpectedMessageResponse;

import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.interceptor.AroundInvoke;
import javax.interceptor.InvocationContext;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;

public abstract class AbstractConversation implements IConversation
{
    private static final long DEFAULT_TIMEOUT = 200000;

    @Inject
    private Event<EConversationTimedOut> conversationTimedOutBus;

    @Inject
    Event<EConversationFailed> conversationFailedBus;

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
        while (true)
        {
            try
            {
                Thread.sleep(timeoutMillis);
                final long delta = System.currentTimeMillis() - lastActivityTime;
                if (delta >= timeoutMillis)
                {
                    getConversationManager().stopConversation(this);
                    conversationTimedOutBus.fireAsync(new EConversationTimedOut(this));
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
            else if (messageClass.equals(UnexpectedMessageResponse.class) ||
                    messageClass.equals(UnauthorizedResponse.class) ||
                    messageClass.equals(ServerErrorResponse.class))
            {
                conversationManager.stopConversation(this);
                conversationFailedBus.fireAsync(new EConversationFailed(this));
                return null;
            }
            else
            {
                sendMessageToPeer(new UnexpectedMessageResponse());
                return null;
            }
        }
        else if (invokedMethodName.equals("start"))
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
            if (invokedMethodName.equals("stop"))
            {
                timeoutWorker.interrupt();
            }
        }
    }
}
