package ru.malnev.gbcloud.client.events;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import ru.malnev.gbcloud.common.conversations.IConversationManager;
import ru.malnev.gbcloud.common.messages.IMessage;
import ru.malnev.gbcloud.common.transport.ITransportChannel;

@Getter
@Setter
@AllArgsConstructor
public class EMessageReceived
{
    private IConversationManager conversationManager;

    private IMessage message;

    private ITransportChannel transportChannel;
}
