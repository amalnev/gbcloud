package ru.malnev.gbcloud.client.conversations;

import org.jetbrains.annotations.NotNull;
import ru.malnev.gbcloud.common.conversations.AbstractConversation;
import ru.malnev.gbcloud.common.conversations.ActiveAgent;
import ru.malnev.gbcloud.common.conversations.Expects;
import ru.malnev.gbcloud.common.conversations.StartsWith;
import ru.malnev.gbcloud.common.messages.IMessage;
import ru.malnev.gbcloud.common.messages.ls.LsRequest;
import ru.malnev.gbcloud.common.messages.ls.LsResponse;

@ActiveAgent
@StartsWith(LsRequest.class)
@Expects(LsResponse.class)
public class LsClientAgent extends AbstractConversation
{
    @Override
    public void processMessageFromPeer(@NotNull IMessage message)
    {
        //TODO: исправить порядок вывода элементов в консоль - сначала директории, потом файлы
        final LsResponse response = (LsResponse) message;
        response.getElements().forEach(element ->
        {
            final StringBuilder builder = new StringBuilder();
            if (element.isDirectory())
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
    }
}
