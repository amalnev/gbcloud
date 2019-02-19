package ru.malnev.gbcloud.client.conversations;

import lombok.Getter;
import lombok.Setter;
import ru.malnev.gbcloud.common.transport.ITransportChannel;

import javax.enterprise.inject.spi.CDI;
import javax.inject.Inject;

public class PingWorker extends Thread
{
    private static final int PING_PERIOD = 1000;

    @Inject
    private ClientConversationManager conversationManager;

    @Override
    public void run()
    {
        while (true)
        {
            try
            {
                Thread.sleep(PING_PERIOD);
                if (!conversationManager.getTransportChannel().isConnected()) continue;
                conversationManager.startConversation(KeepAliveClientAgent.class);
            }
            catch (InterruptedException e)
            {
                break;
            }
        }
    }
}
