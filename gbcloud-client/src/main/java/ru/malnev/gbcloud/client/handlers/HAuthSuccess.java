package ru.malnev.gbcloud.client.handlers;

import ru.malnev.gbcloud.client.command.CLI;
import ru.malnev.gbcloud.client.events.EAuthSuccess;
import ru.malnev.gbcloud.client.logging.ClientLogger;
import ru.malnev.gbcloud.common.conversations.IConversationManager;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.ObservesAsync;
import javax.inject.Inject;
import javax.interceptor.Interceptors;

@ApplicationScoped
@Interceptors(ClientLogger.class)
public class HAuthSuccess
{
    @Inject
    private CLI cli;

    private void handleAuthSuccess(@ObservesAsync final EAuthSuccess event)
    {
        final IConversationManager conversationManager = event.getConversationManager();
        final String server = conversationManager.getTransportChannel().getRemoteAddress();
        cli.setRemoteServer(server);
        cli.setRemoteDirectory("/");
        cli.updatePrompt();
    }
}
