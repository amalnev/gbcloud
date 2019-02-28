package ru.malnev.gbcloud.client.events;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.malnev.gbcloud.common.conversations.IConversationManager;

@NoArgsConstructor
@AllArgsConstructor
public class EAuthSuccess
{
    @Getter
    @Setter
    private IConversationManager conversationManager;
}
