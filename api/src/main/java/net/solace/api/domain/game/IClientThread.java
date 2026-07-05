package net.solace.api.domain.game;

import java.util.concurrent.Callable;
import net.runelite.client.callback.ClientThread;
import net.solace.api.domain.RuneLiteWrapper;

public interface IClientThread
extends RuneLiteWrapper<ClientThread> {
    public void invoke(Runnable var1);

    public <T> T invokeAndWait(Callable<T> var1);
}

