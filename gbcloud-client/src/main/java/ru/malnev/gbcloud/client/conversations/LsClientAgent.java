package ru.malnev.gbcloud.client.conversations;

import org.jetbrains.annotations.NotNull;
import ru.malnev.gbcloud.client.command.CLI;
import ru.malnev.gbcloud.common.conversations.AbstractConversation;
import ru.malnev.gbcloud.common.conversations.ActiveAgent;
import ru.malnev.gbcloud.common.conversations.Expects;
import ru.malnev.gbcloud.common.conversations.StartsWith;
import ru.malnev.gbcloud.common.messages.IMessage;
import ru.malnev.gbcloud.common.messages.ls.LsRequest;
import ru.malnev.gbcloud.common.messages.ls.LsResponse;

import javax.inject.Inject;

@ActiveAgent
@StartsWith(LsRequest.class)
@Expects(LsResponse.class)
public class LsClientAgent extends AbstractConversation
{
    @Inject
    private CLI cli;

    @Override
    public void processMessageFromPeer(@NotNull IMessage message)
    {
        System.out.println();
        final LsResponse response = (LsResponse) message;
        response.getElements().stream()
                .filter(LsResponse.FilesystemElement::isDirectory)
                .forEach(directory -> System.out.println("<DIR>\t\t\t\t\t" + directory.getName()));

        response.getElements().stream()
                .filter(filesystemElement -> !filesystemElement.isDirectory())
                .forEach(file -> System.out.println("\t\t" + file.getSize() + "\t\t\t" + file.getName()));
        cli.updatePrompt();
    }
}
