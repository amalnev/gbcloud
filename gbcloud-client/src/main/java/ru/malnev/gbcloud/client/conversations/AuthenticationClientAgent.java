package ru.malnev.gbcloud.client.conversations;

import org.jetbrains.annotations.NotNull;
import ru.malnev.gbcloud.client.config.ClientConfig;
import ru.malnev.gbcloud.client.events.EAuthFailure;
import ru.malnev.gbcloud.client.events.EAuthSuccess;
import ru.malnev.gbcloud.common.conversations.AbstractConversation;
import ru.malnev.gbcloud.common.conversations.ActiveAgent;
import ru.malnev.gbcloud.common.conversations.Expects;
import ru.malnev.gbcloud.common.conversations.StartsWith;
import ru.malnev.gbcloud.common.messages.IMessage;
import ru.malnev.gbcloud.common.messages.auth.AuthFailResponse;
import ru.malnev.gbcloud.common.messages.auth.AuthMessage;
import ru.malnev.gbcloud.common.messages.auth.AuthSuccessResponse;
import ru.malnev.gbcloud.common.utils.Util;

import javax.enterprise.event.Event;
import javax.inject.Inject;

@ActiveAgent
@StartsWith(AuthMessage.class)
@Expects({AuthFailResponse.class, AuthSuccessResponse.class})
public class AuthenticationClientAgent extends AbstractConversation
{
    @Inject
    private ClientConfig config;

    @Inject
    private Event<EAuthFailure> authFailureBus;

    @Inject
    private Event<EAuthSuccess> authSuccessBus;

    @Override
    protected void beforeStart(@NotNull IMessage initialMessage)
    {
        final AuthMessage authMessage = (AuthMessage) initialMessage;
        authMessage.setLogin(config.getLogin());
        authMessage.setPasswordHash(Util.hash(config.getPassword()));
    }

    @Override
    public synchronized void processMessageFromPeer(@NotNull IMessage message)
    {
        if (message instanceof AuthFailResponse)
        {
            final AuthFailResponse authFailResponse = (AuthFailResponse) message;
            authFailureBus.fireAsync(new EAuthFailure(authFailResponse.getReason()));
        }
        else if (message instanceof AuthSuccessResponse)
        {
            authSuccessBus.fireAsync(new EAuthSuccess(getConversationManager()));
        }
    }
}
