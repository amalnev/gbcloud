package ru.malnev.gbcloud.common.conversations;

import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.malnev.gbcloud.common.messages.IMessage;
import ru.malnev.gbcloud.common.messages.ServerErrorResponse;
import ru.malnev.gbcloud.common.messages.UnexpectedMessageResponse;
import ru.malnev.gbcloud.common.transport.ITransportChannel;
import ru.malnev.gbcloud.common.utils.Util;

import javax.enterprise.inject.spi.CDI;
import javax.enterprise.util.AnnotationLiteral;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

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
    public synchronized void dispatchMessage(final @NotNull IMessage message)
    {
        try
        {
            //Находим диалог, к кторому относится данное сообщение
            final IConversation targetConversation = conversationMap.get(message.getConversationId());

            if (targetConversation != null)
            {
                //Входящее сообщение относится к существующему диалогу. Передаем его
                //туда на обработку
                targetConversation.processMessageFromPeer(message);
            }
            else
            {
                //Входящее сообщение не относится ни к одному из существующих диалогов.

                //Если сообщение не имеет смысла вне контекста одного из существующих
                //диалогов и логически не требует ответа - просто выходим
                if (message.getConversationId() == null) return;
                if (message instanceof UnexpectedMessageResponse) return;

                //Проверяем - возможно, один из наших бинов способен ответить на входящее
                //сообщение в роли @PassiveAgent-а.
                final Class<? extends IConversation> conversationClass = conversationClassMap.get(message.getClass());
                if (conversationClass != null)
                {
                    //Да, подходящий @PassiveAgent нашелся, создаем экземпляр бина.
                    final IConversation newConversation = CDI.current().select(conversationClass, PASSIVE_AGENT_ANNOTATION).get();

                    //Инициализируем диалог, запускаем его и передаем входящее сообщение
                    newConversation.setId(message.getConversationId());
                    startConversation(newConversation);
                    newConversation.processMessageFromPeer(message);
                }
                else
                {
                    //Нет, на данное сообщение некому ответить. Посылаем обратно UnexpectedMessageResponse.
                    final IMessage unexpectedMessageResponse = new UnexpectedMessageResponse();
                    unexpectedMessageResponse.setConversationId(message.getConversationId());
                    transportChannel.sendMessage(unexpectedMessageResponse);
                }
            }
        }
        catch (Exception e)
        {
            //Если в процессе обработки сообщения в диалоге или при инициализации нового диалога
            //возникло исключение - здесь последняя возможность его корректно обработать.
            //При необходимости закрываем соответствующий диалог и отсылаем сообщение
            //об ошибке на удаленную сторону.

            final String conversationId = message.getConversationId();
            if(conversationId == null) return;
            final IConversation failedConversation = conversationMap.get(conversationId);
            if(failedConversation != null) stopConversation(failedConversation);
            try
            {
                final String errorDescription = Util.getErrorDescription(e);
                final IMessage errorMessage = new ServerErrorResponse(errorDescription);
                errorMessage.setConversationId(conversationId);
                transportChannel.sendMessage(errorMessage);
            }
            catch (Exception ex)
            {
                e.printStackTrace();
            }
        }
    }

    @Override
    public synchronized void stopConversation(final @NotNull IConversation conversation)
    {
        conversation.cleanup();
        conversationMap.remove(conversation.getId());
    }

    @Override
    @SneakyThrows
    public synchronized void startConversation(@NotNull IConversation conversation)
    {
        if(getTransportChannel() == null) throw new Exception();
        conversationMap.put(conversation.getId(), conversation);
        conversation.setConversationManager(this);
        conversation.init();
    }
}
