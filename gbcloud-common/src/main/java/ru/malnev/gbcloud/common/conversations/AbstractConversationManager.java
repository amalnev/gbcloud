package ru.malnev.gbcloud.common.conversations;

import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.malnev.gbcloud.common.messages.IMessage;
import ru.malnev.gbcloud.common.messages.UnexpectedMessageResponse;
import ru.malnev.gbcloud.common.transport.ITransportChannel;
import ru.malnev.gbcloud.common.utils.Util;

import javax.enterprise.inject.spi.CDI;
import javax.enterprise.util.AnnotationLiteral;
import java.util.HashMap;
import java.util.Map;

public abstract class AbstractConversationManager implements IConversationManager
{
    private static final AnnotationLiteral<PassiveAgent> PASSIVE_AGENT_ANNOTATION = new AnnotationLiteral<PassiveAgent>() {};

    @Getter
    @Setter
    private ITransportChannel transportChannel;

    private Map<String, IConversation> conversationMap = new HashMap<>();

    private Map<Class<? extends IMessage>, Class<? extends IConversation>> conversationClassMap = new HashMap<>();

    public AbstractConversationManager()
    {
        CDI.current().select(PASSIVE_AGENT_ANNOTATION).forEach(bean ->
        {
            final Class<? extends IConversation> conversationClass = (Class<? extends IConversation>) bean.getClass();
            final Util.AnnotatedClass annotatedClass = Util.getAnnotation(RespondsTo.class, conversationClass);
            if (annotatedClass == null) return;
            conversationClassMap.put(((RespondsTo) annotatedClass.getAnnotation()).value(), annotatedClass.getAnnotatedClass());
        });
    }

    @Override
    public void dispatchMessage(final @NotNull IMessage message)
    {
        IConversation targetConversation = null;
        synchronized (this)
        {
            targetConversation = conversationMap.get(message.getConversationId());
        }

        if (targetConversation != null)
        {
            targetConversation.processMessageFromPeer(message);
        }
        else
        {
            if (message.getConversationId() == null) return;
            if (message instanceof UnexpectedMessageResponse) return;
            IConversation newConversation = null;
            synchronized (this)
            {
                final Class<? extends IConversation> conversationClass = conversationClassMap.get(message.getClass());
                if (conversationClass != null)
                {
                    newConversation = CDI.current().select(conversationClass, PASSIVE_AGENT_ANNOTATION).get();
                }
            }

            if (newConversation == null)
            {
                final IMessage unexpectedMessageResponse = new UnexpectedMessageResponse();
                unexpectedMessageResponse.setConversationId(message.getConversationId());
                transportChannel.sendMessage(unexpectedMessageResponse);
                return;
            }
            newConversation.setId(message.getConversationId());
            newConversation.setConversationManager(this);
            synchronized (this)
            {
                conversationMap.put(newConversation.getId(), newConversation);
            }
            newConversation.start();
            newConversation.processMessageFromPeer(message);
        }
    }

    @Override
    public void stopConversation(@NotNull IConversation conversation)
    {
        conversation.stop();
        synchronized (this)
        {
            conversationMap.remove(conversation.getId());
        }
    }

    @Override
    public void startConversation(@NotNull IConversation conversation)
    {
        synchronized (this)
        {
            conversationMap.put(conversation.getId(), conversation);
        }
        conversation.setConversationManager(this);
        conversation.start();
    }

    @Nullable
    protected synchronized IConversation initiateConversation(final @NotNull IMessage message)
    {
        return null;
    }
}
