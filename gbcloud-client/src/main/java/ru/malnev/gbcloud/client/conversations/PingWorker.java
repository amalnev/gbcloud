package ru.malnev.gbcloud.client.conversations;

import lombok.Getter;
import lombok.Setter;
import ru.malnev.gbcloud.common.transport.ITransportChannel;

import javax.enterprise.inject.spi.CDI;

public class PingWorker extends Thread
{
    private static final int PING_PERIOD = 1000;

    @Getter
    @Setter
    private ITransportChannel transportChannel;

    @Getter
    @Setter
    private ClientConversationManager conversationManager;

    @Override
    public void run()
    {
        while (true)
        {
            try
            {
                Thread.sleep(PING_PERIOD);
                if (!transportChannel.isConnected()) continue;
                final IClientAgent keepAliveAgent = CDI.current().select(KeepAliveClientAgent.class).get();
                keepAliveAgent.setTransportChannel(transportChannel);
                keepAliveAgent.setConversationManager(conversationManager);
                conversationManager.startConversation(keepAliveAgent);
            }
            catch (InterruptedException e)
            {
                break;
            }
        }
    }
}
