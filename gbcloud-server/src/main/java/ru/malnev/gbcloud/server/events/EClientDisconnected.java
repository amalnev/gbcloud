package ru.malnev.gbcloud.server.events;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import ru.malnev.gbcloud.server.context.IClientContext;

@AllArgsConstructor
public class EClientDisconnected
{
    @Getter
    @Setter
    private IClientContext clientContext;
}
