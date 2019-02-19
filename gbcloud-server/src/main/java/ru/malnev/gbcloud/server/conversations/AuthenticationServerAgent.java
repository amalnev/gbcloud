package ru.malnev.gbcloud.server.conversations;

import org.jetbrains.annotations.NotNull;
import ru.malnev.gbcloud.common.conversations.AbstractConversation;
import ru.malnev.gbcloud.common.messages.AuthFailResponse;
import ru.malnev.gbcloud.common.messages.AuthMessage;
import ru.malnev.gbcloud.common.messages.AuthSuccessResponse;
import ru.malnev.gbcloud.common.messages.IMessage;
import ru.malnev.gbcloud.common.transport.ITransportChannel;
import ru.malnev.gbcloud.server.persistence.entitites.User;
import ru.malnev.gbcloud.server.persistence.repositories.UserRepository;

import javax.inject.Inject;

public class AuthenticationServerAgent extends AbstractConversation
{
    private static final String UNKNOWN_USER_MESSAGE = "Unknown user";
    private static final String WRONG_PASSWORD_MESSAGE = "Wrong password";

    @Inject
    private UserRepository userRepository;

    public AuthenticationServerAgent()
    {
        expectMessage(AuthMessage.class);
    }

    @Override
    public void processMessageFromPeer(final @NotNull IMessage message)
    {
        if(message instanceof AuthMessage)
        {
            final ServerConversationManager conversationManager = (ServerConversationManager) getConversationManager();
            final AuthMessage authMessage = (AuthMessage) message;
            final User user = userRepository.findByName(authMessage.getLogin());
            if(user == null)
            {
                final AuthFailResponse unknownUserResponse = new AuthFailResponse();
                unknownUserResponse.setReason(UNKNOWN_USER_MESSAGE);
                sendMessageToPeer(unknownUserResponse);
                conversationManager.stopConversation(this);
                return;
            }

            if(!user.getPasswordHash().equals(authMessage.getPasswordHash()))
            {
                final AuthFailResponse wrongPasswordResponse = new AuthFailResponse();
                wrongPasswordResponse.setReason(WRONG_PASSWORD_MESSAGE);
                sendMessageToPeer(wrongPasswordResponse);
                conversationManager.stopConversation(this);
                return;
            }

            conversationManager.setUser(user);
            sendMessageToPeer(new AuthSuccessResponse());
            conversationManager.stopConversation(this);
        }
    }
}
