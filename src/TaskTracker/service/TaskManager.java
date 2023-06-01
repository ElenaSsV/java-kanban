package TaskTracker.service;

import TaskTracker.model.Epic;
import TaskTracker.model.Subtask;
import TaskTracker.model.Task;

import java.util.List;
import java.util.Optional;


public interface TaskManager {

    int createTask(Task task);

    int createEpic(Epic epic);

    int createSubtask(Subtask subtask);

    void updateTask(Task task);

    void updateEpic(Epic epic);

    void updateSubtask(Subtask subtask);

    Task getTaskById(int id);

    Epic getEpicById(int id);

    Subtask getSubtaskById(int id);

    void removeTaskById(int id);

    void removeEpicById(int epicId);

    void removeSubtaskById(int subtaskId);

    void removeAllTasks();

    void removeAllEpics();

    void removeAllSubtasks();

    List<Task> getAllTasks();

    List<Epic> getAllEpics();

    List<Subtask> getAllSubtasks();

    List<Subtask> getSubtasksToEpic(int epicId);

    List<Task> getHistory();


}

