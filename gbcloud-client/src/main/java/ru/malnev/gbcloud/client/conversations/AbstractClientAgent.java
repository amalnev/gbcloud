package ru.malnev.gbcloud.client.conversations;

import lombok.Getter;
import lombok.Setter;
import ru.malnev.gbcloud.common.conversations.AbstractConversation;
import ru.malnev.gbcloud.common.transport.ITransportChannel;

public abstract class AbstractClientAgent extends AbstractConversation implements IClientAgent
{
    @Getter
    @Setter
    private ITransportChannel transportChannel;
}
