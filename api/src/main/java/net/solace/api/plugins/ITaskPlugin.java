package net.solace.api.plugins;

public interface ITaskPlugin extends ILoopedPlugin {
    Task[] getTasks();
}
