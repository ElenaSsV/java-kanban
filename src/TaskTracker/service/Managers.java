package TaskTracker.service;
import TaskTracker.model.*;


import java.util.List;

public final class Managers {

    public static TaskManager getDefault() {
        return new InMemoryTaskManager();
    }

    public static List<Task> getDefaultHistory() {
        return InMemoryHistoryManager.viewedTasks;
    }

}
