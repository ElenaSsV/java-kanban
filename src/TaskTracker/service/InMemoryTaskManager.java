package TaskTracker.service;

import TaskTracker.model.Epic;
import TaskTracker.model.Subtask;
import TaskTracker.model.Task;
import TaskTracker.model.Status;

import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;


public class InMemoryTaskManager implements TaskManager {

    private final Map<Integer, Task> tasks = new HashMap<>();
    private final Map<Integer, Epic> epics = new HashMap<>();
    private final Map<Integer, Subtask> subtasks = new HashMap<>();

    private final HistoryManager historyManager = Managers.getDefaultHistory();

    private int id = 1;

    @Override
    public void createTask(Task task) {
        task.setId(id++);
        tasks.put(task.getId(), task);
    }

    @Override
    public void createEpic(Epic epic) {
        epic.setId(id++);
        epics.put(epic.getId(), epic);
    }

   @Override
    public void createSubtask(Subtask subtask) {
        subtask.setId(id++);
        subtasks.put(subtask.getId(), subtask);

        int epicId = subtask.getEpicId();

        Epic epic = epics.get(epicId);
        epic.getSubtaskIds().add(subtask.getId());
        updateEpicStatus(epicId);
    }

    @Override
    public void updateTask(Task task) {
        tasks.put(task.getId(), task);
    }

    @Override
    public void updateEpic(Epic epic) {
        epics.put(epic.getId(), epic);
    }

   @Override
    public void updateSubtask(Subtask subtask) {
        subtasks.put(subtask.getId(), subtask);
        updateEpicStatus(subtask.getEpicId());
    }

    @Override
    public Task getTaskById(int id) {
        historyManager.addTask(tasks.get(id));
        return tasks.get(id);
    }

    @Override
    public Epic getEpicById(int id) {
        historyManager.addTask(epics.get(id));
        return epics.get(id);
    }

    @Override
    public Subtask getSubtaskById(int id) {
        historyManager.addTask(subtasks.get(id));
        return subtasks.get(id);
    }

    @Override
    public void removeTaskById(int taskId) {
        tasks.remove(taskId);
        historyManager.remove(taskId);
    }

    @Override
    public void removeEpicById(int epicId) {
        List<Integer> subIdsToRemove = epics.get(epicId).getSubtaskIds();
        if (subIdsToRemove.isEmpty()) {
            return;
        }
        for (Integer id : subIdsToRemove) {
            subtasks.remove(id);
            historyManager.remove(id);
        }
        epics.remove(epicId);
        historyManager.remove(epicId);
    }

   @Override
    public void removeSubtaskById(int subtaskId) {
        int epicId = subtasks.get(subtaskId).getEpicId();

        epics.get(epicId).getSubtaskIds().remove(Integer.valueOf(subtaskId));
        historyManager.remove(subtaskId);
        subtasks.remove(subtaskId);

        updateEpicStatus(epicId);
    }

    @Override
    public void removeAllTasks() {
        for (Integer taskId : tasks.keySet()) {
            historyManager.remove(taskId);
        }
        tasks.clear();
    }

    @Override
    public void removeAllEpics() {
        for (Integer epicId : epics.keySet()) {
            historyManager.remove(epicId);
        }
        for (Integer subtaskId : subtasks.keySet()) {
            historyManager.remove(subtaskId);
        }
        epics.clear();
        subtasks.clear();
    }

    @Override
    public void removeAllSubtasks() {
        for (Integer subtaskId : subtasks.keySet()) {
            historyManager.remove(subtaskId);
        }
        subtasks.clear();
        for (Epic epic : epics.values()) {
            epic.getSubtaskIds().clear();
            epic.setStatus(Status.NEW);
        }
    }

    @Override
    public List<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public List<Epic> getAllEpics() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public List<Subtask>getAllSubtasks(){
        return new ArrayList<>(subtasks.values());
    }

    @Override
    public List<Subtask> getSubtasksToEpic(int epicId) {
        List<Subtask> allSubtasksToEpic = new ArrayList<>();

        for (Integer subtaskId : epics.get(epicId).getSubtaskIds()) {
            allSubtasksToEpic.add(subtasks.get(subtaskId));
        }
        return allSubtasksToEpic;
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    private void updateEpicStatus(int epicId) {

        int countNew = 0;
        int countDone = 0;

        Epic epic = epics.get(epicId);

        for (Integer subtaskId : epic.getSubtaskIds()) {
           Status status = subtasks.get(subtaskId).getStatus();

            switch (status) {
                case NEW:
                    countNew++;
                        break;
                case DONE:
                    countDone++;
                       break;
                default:
                    epic.setStatus(Status.IN_PROGRESS);
                    return;
            }
        }

        if (countNew == epic.getSubtaskIds().size()) {
            epic.setStatus(Status.NEW);
        } else if (countDone == epic.getSubtaskIds().size()) {
            epic.setStatus(Status.DONE);
        }
        else {
            epic.setStatus(Status.IN_PROGRESS);
        }
    }
}

