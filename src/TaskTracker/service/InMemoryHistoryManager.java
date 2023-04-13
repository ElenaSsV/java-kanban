package TaskTracker.service;

import TaskTracker.model.Task;

import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {

    protected static List<Task> viewedTasks = new ArrayList<>();

    @Override
    public void addTask(Task task) {
        if (viewedTasks.size() == 10) {
            viewedTasks.remove(0);
            viewedTasks.add(task);
        } else {
            viewedTasks.add(task);
        }
    }

    @Override
    public List<Task> getHistory() {
        return  Managers.getDefaultHistory();
    }
}
