package ru.malnev.gbcloud.client.handlers;

import ru.malnev.gbcloud.client.conversations.ClientConversationManager;
import ru.malnev.gbcloud.client.conversations.MkdirClientAgent;
import ru.malnev.gbcloud.client.logging.ClientLogger;
import ru.malnev.gbcloud.common.conversations.ActiveAgent;
import ru.malnev.gbcloud.common.events.EDirectoryCreated;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.ObservesAsync;
import javax.enterprise.inject.spi.CDI;
import javax.enterprise.util.AnnotationLiteral;
import javax.inject.Inject;
import javax.interceptor.Interceptors;

@ApplicationScoped
@Interceptors(ClientLogger.class)
public class HDirectoryCreated
{
    private static final AnnotationLiteral<ActiveAgent> ACTIVE_AGENT_ANNOTATION = new AnnotationLiteral<ActiveAgent>() {};

    @Inject
    private ClientConversationManager conversationManager;

    private void handleDirectoryCreated(@ObservesAsync final EDirectoryCreated event)
    {
        final MkdirClientAgent agent = CDI.current().select(MkdirClientAgent.class, ACTIVE_AGENT_ANNOTATION).get();
        agent.setDirectoryPath(event.getLocalRelativePath());
        conversationManager.startConversation(agent);
    }
}
