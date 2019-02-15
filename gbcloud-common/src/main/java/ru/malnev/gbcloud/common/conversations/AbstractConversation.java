package ru.malnev.gbcloud.common.conversations;

import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;
import ru.malnev.gbcloud.common.messages.IMessage;
import ru.malnev.gbcloud.common.messages.UnexpectedMessageResponse;
import ru.malnev.gbcloud.common.transport.ITransportChannel;

import javax.interceptor.AroundInvoke;
import javax.interceptor.InvocationContext;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;

public abstract class AbstractConversation implements IConversation
{
    @Getter
    @Setter
    private String id = UUID.randomUUID().toString();

    @Getter
    @Setter
    private IConversationManager conversationManager;

    private Set<Class<? extends IMessage>> expectedMessages = new LinkedHashSet<>();

    protected void expectMessage(final @NotNull Class<? extends IMessage> messageClass)
    {
        expectedMessages.add(messageClass);
    }

    protected void stopExpectingMessage(final @NotNull Class<? extends IMessage> messageClass)
    {
        expectedMessages.remove(messageClass);
    }

    @AroundInvoke
    @SneakyThrows
    private Object wrapProcessMessage(final InvocationContext invocationContext)
    {
        if (invocationContext.getMethod().getName().equals("processMessage"))
        {
            final Class messageClass = invocationContext.getParameters()[0].getClass();
            if (expectedMessages.contains(messageClass))
            {
                expectedMessages.remove(messageClass);
            }
            else
            {
                final ITransportChannel transportChannel = (ITransportChannel) invocationContext.getParameters()[1];
                final IMessage response = new UnexpectedMessageResponse();
                transportChannel.sendMessage(response);
            }
        }

        return invocationContext.proceed();
    }
}
