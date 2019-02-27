package ru.malnev.gbcloud.server.events;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import ru.malnev.gbcloud.common.messages.IMessage;
import ru.malnev.gbcloud.server.conversations.ServerConversationManager;

@Getter
@Setter
@AllArgsConstructor
public class EMessageReceived
{
    private ServerConversationManager conversationManager;

    private IMessage message;
}
