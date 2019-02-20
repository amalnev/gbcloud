package ru.malnev.gbcloud.client.conversations;

import org.jetbrains.annotations.NotNull;
import ru.malnev.gbcloud.client.config.ClientConfig;
import ru.malnev.gbcloud.client.events.EAuthFailure;
import ru.malnev.gbcloud.client.events.EAuthSuccess;
import ru.malnev.gbcloud.common.conversations.AbstractConversation;
import ru.malnev.gbcloud.common.messages.AuthFailResponse;
import ru.malnev.gbcloud.common.messages.AuthMessage;
import ru.malnev.gbcloud.common.messages.AuthSuccessResponse;
import ru.malnev.gbcloud.common.messages.IMessage;
import ru.malnev.gbcloud.common.utils.Util;

import javax.enterprise.event.Event;
import javax.inject.Inject;

public class AuthenticationClientAgent extends AbstractConversation
{
    @Inject
    private ClientConfig config;

    @Inject
    private Event<EAuthFailure> authFailureBus;

    @Inject
    private Event<EAuthSuccess> authSuccessBus;

    @Override
    public synchronized void start()
    {
        expectMessage(AuthFailResponse.class);
        expectMessage(AuthSuccessResponse.class);
        final IMessage outgoingMessage = new AuthMessage(config.getLogin(),
                Util.hash(config.getPassword()));
        sendMessageToPeer(outgoingMessage);
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
            authSuccessBus.fireAsync(new EAuthSuccess());
        }

        getConversationManager().stopConversation(this);
    }
}
