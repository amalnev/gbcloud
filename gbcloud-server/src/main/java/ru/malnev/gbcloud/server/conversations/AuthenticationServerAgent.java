package ru.malnev.gbcloud.server.conversations;

import org.jetbrains.annotations.NotNull;
import ru.malnev.gbcloud.common.conversations.RespondsTo;
import ru.malnev.gbcloud.common.messages.IMessage;
import ru.malnev.gbcloud.common.messages.auth.AuthFailResponse;
import ru.malnev.gbcloud.common.messages.auth.AuthMessage;
import ru.malnev.gbcloud.common.messages.auth.AuthSuccessResponse;
import ru.malnev.gbcloud.server.persistence.entitites.User;
import ru.malnev.gbcloud.server.persistence.repositories.UserRepository;

import javax.inject.Inject;
import java.nio.file.Paths;

@RespondsTo(AuthMessage.class)
public class AuthenticationServerAgent extends ServerAgent
{
    private static final String UNKNOWN_USER_MESSAGE = "Unknown user";
    private static final String WRONG_PASSWORD_MESSAGE = "Wrong password";

    @Inject
    private UserRepository userRepository;

    @Override
    public void processMessageFromPeer(final @NotNull IMessage message)
    {
        if (message instanceof AuthMessage)
        {
            final ServerConversationManager conversationManager = (ServerConversationManager) getConversationManager();
            final AuthMessage authMessage = (AuthMessage) message;
            final User user = userRepository.findByName(authMessage.getLogin());
            IMessage response = null;
            if (user == null)
            {
                final AuthFailResponse unknownUserResponse = new AuthFailResponse();
                unknownUserResponse.setReason(UNKNOWN_USER_MESSAGE);
                response = unknownUserResponse;
            }
            else if (!user.getPasswordHash().equals(authMessage.getPasswordHash()))
            {
                final AuthFailResponse wrongPasswordResponse = new AuthFailResponse();
                wrongPasswordResponse.setReason(WRONG_PASSWORD_MESSAGE);
                response = wrongPasswordResponse;
            }
            else
            {
                conversationManager.setUser(user);
                conversationManager.getServerDirectory().setRootDirectory(Paths.get(user.getHomeDirectory()));
                response = new AuthSuccessResponse();
            }

            sendMessageToPeer(response);
        }
    }
}
