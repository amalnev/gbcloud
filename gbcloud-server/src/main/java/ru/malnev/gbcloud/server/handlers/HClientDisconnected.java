package ru.malnev.gbcloud.server.handlers;


import ru.malnev.gbcloud.server.events.EClientDisconnected;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.ObservesAsync;

@ApplicationScoped
public class HClientDisconnected
{
    private void handleClientDisconnected(@ObservesAsync final EClientDisconnected event)
    {

    }
}
