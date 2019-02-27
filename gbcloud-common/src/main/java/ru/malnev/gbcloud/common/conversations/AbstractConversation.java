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
import ru.malnev.gbcloud.common.messages.auth.UnauthorizedResponse;
import ru.malnev.gbcloud.common.messages.UnexpectedMessageResponse;
import ru.malnev.gbcloud.common.utils.Util;

import javax.enterprise.event.Event;
import javax.enterprise.inject.spi.CDI;
import javax.inject.Inject;
import javax.interceptor.AroundInvoke;
import javax.interceptor.InvocationContext;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;

public abstract class AbstractConversation implements IConversation
{
    private static final long DEFAULT_TIMEOUT = 2000000;

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

    private volatile boolean requiresMoreMessages = true;

    @Getter
    @Setter
    private volatile long timeoutMillis = DEFAULT_TIMEOUT;

    @Getter
    private volatile int messagesSent = 0;

    @Getter
    private volatile int messagesReceived = 0;

    private Set<Class<? extends IMessage>> expectedMessageClasses = new LinkedHashSet<>();

    @Override
    public synchronized boolean timedOut()
    {
        final long delta = System.currentTimeMillis() - lastActivityTime;
        return delta >= timeoutMillis;
    }

    public synchronized void continueConversation()
    {
        requiresMoreMessages = true;
    }

    protected synchronized void beforeStart(final @NotNull IMessage initialMessage)
    {

    }

    protected synchronized void beforeFinish()
    {

    }

    public synchronized void stopConversation()
    {
        getConversationManager().stopConversation(this);
    }

    @Override
    public synchronized void sendMessageToPeer(final @NotNull IMessage message)
    {
        message.setConversationId(getId());
        getConversationManager().getTransportChannel().sendMessage(message);
        messagesSent++;
    }

    @Override
    @SneakyThrows
    public synchronized void init()
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
            expectedMessageClasses.addAll(Arrays.asList(expectedMessages));
        }

        lastActivityTime = System.currentTimeMillis();

        messagesSent = 0;
        messagesReceived = 0;

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
            expectedMessageClasses.add(initialMessageClass);
        }
    }

    @Override
    public synchronized void cleanup()
    {
        beforeFinish();
    }

    private final static String PROCESS_MESSAGE_METHOD_NAME = "processMessageFromPeer";
    private final static String UNEXPECTED_MESSAGE_REASON = "Last message was rejected by server";
    private final static String UNAUTHORIZED_REASON = "Unauthorized";

    @AroundInvoke
    @SneakyThrows
    private synchronized Object wrapMethods(final InvocationContext invocationContext)
    {
        final String invokedMethodName = invocationContext.getMethod().getName();
        if (invokedMethodName.equals(PROCESS_MESSAGE_METHOD_NAME))
        {
            messagesReceived++;
            lastActivityTime = System.currentTimeMillis();
            final Class messageClass = invocationContext.getParameters()[0].getClass();
            if (expectedMessageClasses.contains(messageClass))
            {
                requiresMoreMessages = false;
            }
            else if (messageClass.equals(UnexpectedMessageResponse.class) ||
                    messageClass.equals(UnauthorizedResponse.class) ||
                    messageClass.equals(ServerErrorResponse.class))
            {
                stopConversation();
                final EConversationFailed event = new EConversationFailed(this);
                event.setRemote(true);
                if(messageClass.equals(UnexpectedMessageResponse.class))
                {
                    event.setReason(UNEXPECTED_MESSAGE_REASON);
                }
                else if(messageClass.equals(UnauthorizedResponse.class))
                {
                    event.setReason(UNAUTHORIZED_REASON);
                }
                else if(messageClass.equals(ServerErrorResponse.class))
                {
                    final ServerErrorResponse response = (ServerErrorResponse) invocationContext.getParameters()[0];
                    event.setReason(response.getReason());
                }
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
            if(!requiresMoreMessages)
            {
                stopConversation();
                conversationCompleteBus.fireAsync(new EConversationComplete(this));
            }
        }

        return result;
    }
}
