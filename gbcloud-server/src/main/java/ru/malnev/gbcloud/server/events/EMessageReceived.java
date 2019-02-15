package ru.malnev.gbcloud.server.events;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import ru.malnev.gbcloud.common.messages.IMessage;
import ru.malnev.gbcloud.server.context.IClientContext;

@Getter
@Setter
@AllArgsConstructor
public class EMessageReceived
{
    private IClientContext clientContext;

    private IMessage message;
}
