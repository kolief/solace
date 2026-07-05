package net.solace.api.interact;

import java.util.Queue;
import net.solace.api.interact.Automation;
import net.solace.api.interact.InteractMethod;
import net.solace.api.interact.mouse.MouseManager;

public interface InteractManager {
    public InteractMethod getInteractMethodOverride();

    public void setInteractMethodOverride(InteractMethod var1);

    public void queue(Automation var1);

    public Queue<Automation> getQueue();

    public MouseManager getMouseManager();

    public void setMouseManager(MouseManager var1);

    public boolean isInputIdle();
}

