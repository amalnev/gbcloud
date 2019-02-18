package ru.malnev.gbcloud.client.conversations;

import org.jetbrains.annotations.NotNull;
import ru.malnev.gbcloud.common.messages.IMessage;
import ru.malnev.gbcloud.common.messages.KeepAliveMessage;
import ru.malnev.gbcloud.common.transport.ITransportChannel;

public class KeepAliveClientAgent extends AbstractClientAgent
{
    private final static int TIMEOUT = 2000;

    private long startTime;

    private Thread timeoutWorker = new Thread(() ->
    {
        try
        {
            Thread.sleep(TIMEOUT);
        }
        catch (InterruptedException e)
        {
            return;
        }
        getConversationManager().stopConversation(this);
    });

    @Override
    public synchronized void processMessage(final @NotNull IMessage message,
                               final @NotNull ITransportChannel transportChannel)
    {
        final long delta = System.currentTimeMillis() - startTime;
        System.out.println("Response from " +
                transportChannel.getRemoteAddress() +
                ". rtt=" +
                delta +
                "ms");
        getConversationManager().stopConversation(this);
    }

    @Override
    public synchronized void start()
    {
        startTime = System.currentTimeMillis();
        timeoutWorker.start();
        expectMessage(KeepAliveMessage.class);
        final IMessage outgoingMessage = new KeepAliveMessage();
        outgoingMessage.setConversationId(getId());
        getTransportChannel().sendMessage(outgoingMessage);
    }
}
