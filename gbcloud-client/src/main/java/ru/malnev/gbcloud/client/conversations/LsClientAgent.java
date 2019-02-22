package ru.malnev.gbcloud.client.conversations;

import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import ru.malnev.gbcloud.common.conversations.AbstractConversation;
import ru.malnev.gbcloud.common.conversations.ActiveAgent;
import ru.malnev.gbcloud.common.messages.IMessage;
import ru.malnev.gbcloud.common.messages.LsRequest;
import ru.malnev.gbcloud.common.messages.LsResponse;
import ru.malnev.gbcloud.common.messages.ServerOkResponse;

import javax.inject.Inject;

@ActiveAgent
public class LsClientAgent extends AbstractConversation
{
    @Inject
    private LsRequest request;

    @Override
    public void processMessageFromPeer(@NotNull IMessage message)
    {
        final LsResponse response = (LsResponse) message;
        response.getElements().forEach(element ->
        {
            final StringBuilder builder = new StringBuilder();
            if(element.isDirectory())
            {
                builder.append("<DIR>\t\t");
            }
            else
            {
                builder.append("\t\t");
                builder.append(element.getSize());
                builder.append("\t");
            }
            builder.append(element.getName());
            System.out.println(builder.toString());
        });
        getConversationManager().stopConversation(this);
    }

    @Override
    public void start()
    {
        expectMessage(LsResponse.class);
        sendMessageToPeer(request);
    }
}
