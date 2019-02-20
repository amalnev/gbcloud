package ru.malnev.gbcloud.client.events;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import ru.malnev.gbcloud.common.messages.IMessage;

@Getter
@Setter
@AllArgsConstructor
public class EMessageReceived
{
    private IMessage message;
}
