package ru.malnev.gbcloud.client.handlers;

import ru.malnev.gbcloud.client.conversations.ClientConversationManager;
import ru.malnev.gbcloud.client.conversations.RmClientAgent;
import ru.malnev.gbcloud.client.logging.ClientLogger;
import ru.malnev.gbcloud.common.conversations.ActiveAgent;
import ru.malnev.gbcloud.common.events.EFilesystemItemDeleted;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.ObservesAsync;
import javax.enterprise.inject.spi.CDI;
import javax.enterprise.util.AnnotationLiteral;
import javax.inject.Inject;
import javax.interceptor.Interceptors;

@ApplicationScoped
@Interceptors(ClientLogger.class)
public class HFilesystemItemDeleted
{
    private static final AnnotationLiteral<ActiveAgent> ACTIVE_AGENT_ANNOTATION = new AnnotationLiteral<ActiveAgent>() {};

    @Inject
    private ClientConversationManager conversationManager;

    private void handleFilesystemItemDeleted(@ObservesAsync final EFilesystemItemDeleted event)
    {
        final RmClientAgent agent = CDI.current().select(RmClientAgent.class, ACTIVE_AGENT_ANNOTATION).get();
        agent.setTargetPath(event.getLocalRelativePath());
        conversationManager.startConversation(agent);
    }
}
