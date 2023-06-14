package TaskTracker.service;

import TaskTracker.client.KVTaskClient;
import TaskTracker.model.Epic;
import TaskTracker.model.Subtask;
import TaskTracker.model.Task;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class HttpTaskManager extends FileBackedTaskManager {

    private KVTaskClient client;
    private Gson gson = Managers.getGson();

    public HttpTaskManager(String url) throws IOException, InterruptedException {
        super(url);
        client = new KVTaskClient(url);
    }

    @Override
    public void save() {

        String allTasksStr = gson.toJson(getAllTasks());
        String allEpicsStr = gson.toJson(getAllEpics());
        String allSubtasksStr = gson.toJson(getAllSubtasks());
        String historyStr = gson.toJson(getHistory());
        try {
            client.put("Tasks", allTasksStr);
            client.put("Epics", allEpicsStr);
            client.put("Subtasks", allSubtasksStr);
            client.put("History", historyStr);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static HttpTaskManager loadFromServer(String url) throws IOException, InterruptedException {
        HttpTaskManager manager = new HttpTaskManager(url);

        Type taskType = new TypeToken<ArrayList<Task>>() {}.getType();
        List<Task> tasksFromJson = manager.gson.fromJson(manager.client.load("Tasks"), taskType);
        if (tasksFromJson != null) {
            for (Task task : tasksFromJson) {
                manager.addTask(task);
            }
        }

        Type epicType = new TypeToken<ArrayList<Epic>>() {}.getType();
        List<Epic> epicsFromJson = manager.gson.fromJson(manager.client.load("Epics"), epicType);
        if (epicsFromJson != null) {
            for (Epic epic : epicsFromJson) {
                manager.addTask(epic);
            }
        }

        Type subtaskType = new TypeToken<ArrayList<Subtask>>() {}.getType();
        List<Subtask> subtasksFromJson = manager.gson.fromJson(manager.client.load("Subtasks"), subtaskType);
        if (subtasksFromJson != null) {
            for (Subtask subtask : subtasksFromJson) {
                manager.addTask(subtask);
            }
        }

        List<Task> viewedTasks = manager.gson.fromJson(manager.client.load("History"), taskType);
        if (viewedTasks != null) {
            for (Task viewedTask : viewedTasks) {
                manager.historyManager.addTask(viewedTask);
            }
        }
        return manager;
    }

}
