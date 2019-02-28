package ru.malnev.gbcloud.client.conversations;

import org.jetbrains.annotations.NotNull;
import ru.malnev.gbcloud.client.command.CLI;
import ru.malnev.gbcloud.common.conversations.AbstractConversation;
import ru.malnev.gbcloud.common.conversations.ActiveAgent;
import ru.malnev.gbcloud.common.conversations.Expects;
import ru.malnev.gbcloud.common.conversations.StartsWith;
import ru.malnev.gbcloud.common.messages.IMessage;
import ru.malnev.gbcloud.common.messages.pwd.PwdRequest;
import ru.malnev.gbcloud.common.messages.pwd.PwdResponse;

import javax.inject.Inject;

@ActiveAgent
@StartsWith(PwdRequest.class)
@Expects(PwdResponse.class)
public class PwdClientAgent extends AbstractConversation
{
    @Inject
    private CLI cli;

    @Override
    public void processMessageFromPeer(@NotNull IMessage message)
    {
        final PwdResponse response = (PwdResponse) message;
        System.out.println();
        System.out.println("Current remote dir is " + response.getCurrentDirectory());
        cli.updatePrompt();
    }
}
