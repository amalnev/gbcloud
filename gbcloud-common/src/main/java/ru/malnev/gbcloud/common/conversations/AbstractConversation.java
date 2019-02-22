package ru.malnev.gbcloud.common.conversations;

import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;
import ru.malnev.gbcloud.common.events.EConversationComplete;
import ru.malnev.gbcloud.common.events.EConversationFailed;
import ru.malnev.gbcloud.common.events.EConversationTimedOut;
import ru.malnev.gbcloud.common.messages.IMessage;
import ru.malnev.gbcloud.common.messages.ServerErrorResponse;
import ru.malnev.gbcloud.common.messages.UnauthorizedResponse;
import ru.malnev.gbcloud.common.messages.UnexpectedMessageResponse;
import ru.malnev.gbcloud.common.utils.Util;

import javax.enterprise.event.Event;
import javax.enterprise.inject.spi.CDI;
import javax.inject.Inject;
import javax.interceptor.AroundInvoke;
import javax.interceptor.InvocationContext;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;

public abstract class AbstractConversation implements IConversation
{
    private static final long DEFAULT_TIMEOUT = 2000000;

    @Inject
    private Event<EConversationTimedOut> conversationTimedOutBus;

    @Inject
    private Event<EConversationFailed> conversationFailedBus;

    @Inject
    private Event<EConversationComplete> conversationCompleteBus;

    @Getter
    @Setter
    private volatile String id = UUID.randomUUID().toString();

    @Getter
    @Setter
    private volatile IConversationManager conversationManager;

    private volatile long lastActivityTime = 0;

    private volatile boolean active = true;

    @Getter
    @Setter
    private volatile long timeoutMillis = DEFAULT_TIMEOUT;

    //TODO: Запускать отдельный поток, следящий за таймаутом для каждого диалога - не очень эффективное
    //решение. Лучше сделать один "чистящий" поток в AbstractConversationManager, который будет находить
    //и вычищать неактивные диалоги a la garbage collector
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

    protected void continueConversation()
    {
        active = true;
    }

    protected void beforeStart(final @NotNull IMessage initialMessage)
    {

    }

    @Override
    public void sendMessageToPeer(final @NotNull IMessage message)
    {
        message.setConversationId(getId());
        getConversationManager().getTransportChannel().sendMessage(message);
    }

    @Override
    @SneakyThrows
    public void init()
    {
        //TODO: добавить верификацию this-бина на корректность использования аннотаций @ActiveAgent,
        //@PassiveAgent, @StartsWith, @RespondsTo.

        final Util.AnnotatedClass annotatedWithExpects = Util.getAnnotation(Expects.class, getClass());
        final Util.AnnotatedClass annotatedWithPassiveAgent = Util.getAnnotation(ActiveAgent.class, getClass());
        final Util.AnnotatedClass annotatedWithActiveAgent = Util.getAnnotation(ActiveAgent.class, getClass());
        final Util.AnnotatedClass annotatedWithStarts = Util.getAnnotation(StartsWith.class, getClass());
        final Util.AnnotatedClass annotatedWithRespondsTo = Util.getAnnotation(RespondsTo.class, getClass());

        if(annotatedWithExpects != null)
        {
            final Expects expectsAnnotation = (Expects) annotatedWithExpects.getAnnotation();
            final Class<? extends IMessage>[] expectedMessages = expectsAnnotation.value();
            for (final Class<? extends IMessage> expectedMessage : expectedMessages) expectMessage(expectedMessage);
        }

        lastActivityTime = System.currentTimeMillis();
        timeoutWorker.start();

        if(annotatedWithStarts != null)
        {
            final StartsWith startsWithAnnotation = (StartsWith) annotatedWithStarts.getAnnotation();
            final Class<? extends IMessage> initialMessageClass = startsWithAnnotation.value();
            final IMessage initialMessage = CDI.current().select(initialMessageClass).get();
            beforeStart(initialMessage);
            sendMessageToPeer(initialMessage);
        }
        else if(annotatedWithRespondsTo != null)
        {
            final RespondsTo respondsToAnnotation = (RespondsTo) annotatedWithRespondsTo.getAnnotation();
            final Class<? extends IMessage> initialMessageClass = respondsToAnnotation.value();
            expectMessage(initialMessageClass);
        }
    }

    @Override
    public void cleanup()
    {
        timeoutWorker.interrupt();
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
                active = false;
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

        final Object result = invocationContext.proceed();

        if (invokedMethodName.equals("processMessageFromPeer"))
        {
            if(!active)
            {
                getConversationManager().stopConversation(this);
                conversationCompleteBus.fireAsync(new EConversationComplete(this));
            }
        }

        return result;
    }
}
