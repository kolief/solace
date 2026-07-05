package net.solace.api.plugins;

import java.util.ArrayList;
import java.util.Iterator;
import net.solace.api.plugins.LoopedPlugin;
import net.solace.api.plugins.Task;

public abstract class TaskPlugin
extends LoopedPlugin {
    private Task currentTask = null;

    @Override
    public int loop() {
        ArrayList<Task> tasksToExecute = new ArrayList<Task>();
        for (Task task : this.getTasks()) {
            if (!task.validate()) continue;
            tasksToExecute.add(task);
            if (task.isBlocking()) break;
        }
        Iterator iterator = tasksToExecute.iterator();
        while (iterator.hasNext()) {
            Task task;
            this.currentTask = task = (Task)iterator.next();
            int delay = task.execute();
            if (!task.isBlocking()) continue;
            return delay;
        }
        this.currentTask = null;
        return 1000;
    }

    public abstract Task[] getTasks();

    public Task getCurrentTask() {
        return this.currentTask;
    }
}

