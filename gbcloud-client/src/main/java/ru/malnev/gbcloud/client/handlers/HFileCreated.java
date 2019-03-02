package ru.malnev.gbcloud.client.handlers;

import ru.malnev.gbcloud.client.conversations.ClientConversationManager;
import ru.malnev.gbcloud.client.conversations.PutClientAgent;
import ru.malnev.gbcloud.client.logging.ClientLogger;
import ru.malnev.gbcloud.common.conversations.ActiveAgent;
import ru.malnev.gbcloud.common.events.EFileCreated;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.ObservesAsync;
import javax.enterprise.inject.spi.CDI;
import javax.enterprise.util.AnnotationLiteral;
import javax.inject.Inject;
import javax.interceptor.Interceptors;
import java.nio.file.Paths;

@ApplicationScoped
@Interceptors(ClientLogger.class)
public class HFileCreated
{
    private static final AnnotationLiteral<ActiveAgent> ACTIVE_AGENT_ANNOTATION = new AnnotationLiteral<ActiveAgent>() {};

    @Inject
    private ClientConversationManager conversationManager;

    private void handleFileCreated(@ObservesAsync final EFileCreated event)
    {
        final PutClientAgent agent = CDI.current().select(PutClientAgent.class, ACTIVE_AGENT_ANNOTATION).get();
        agent.setLocalRoot(Paths.get(event.getLocalRoot()));
        agent.setRelativeFilePath(Paths.get(event.getLocalRelativePath()));
        conversationManager.startConversation(agent);
    }
}
