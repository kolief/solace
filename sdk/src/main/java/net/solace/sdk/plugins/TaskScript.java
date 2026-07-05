package net.solace.sdk.plugins;

import lombok.Getter;
import net.solace.api.plugins.ITaskScript;
import net.solace.api.plugins.Task;

public abstract class TaskScript extends Script implements ITaskScript {
    @Getter
    private Class<?> currentTask = null;

    public abstract Task[] getTasks();

    @Override
    public int loop() {
        for (Task task : getTasks()) {
            if (task.validate()) {
                currentTask = task.getClass();
                int delay = task.execute();
                if (task.isBlocking()) {
                    return delay;
                }
            }
        }

        currentTask = null;

        return 1000;
    }
}
