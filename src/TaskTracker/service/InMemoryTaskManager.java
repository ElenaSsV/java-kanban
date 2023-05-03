package TaskTracker.service;

import TaskTracker.model.Epic;
import TaskTracker.model.Subtask;
import TaskTracker.model.Task;
import TaskTracker.model.Status;

import java.util.ArrayList;
import java.util.HashMap;


public class InMemoryTaskManager implements TaskManager {

    private final HashMap<Integer, Task> tasks = new HashMap<>();
    private final HashMap<Integer, Epic> epics = new HashMap<>();
    private final HashMap<Integer, Subtask> subtasks = new HashMap<>();

    private static int id = 1;
    private final HistoryManager inMemoryHistoryManager = Managers.getDefaultHistory();


    @Override
    public void createTask(Task task) {
        task.setStatus(Status.NEW);
        task.setId(id++);
        tasks.put(task.getId(), task);
    }

    @Override
    public void createEpic(Epic epic) {
        epic.setStatus(Status.NEW);
        epic.setId(id++);
        epics.put(epic.getId(), epic);
    }

   @Override
    public void createSubtask(Subtask subtask) {
        subtask.setStatus(Status.NEW);
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
        inMemoryHistoryManager.addTask(tasks.get(id));
        return tasks.get(id);
    }

    @Override
    public Epic getEpicById(int id) {
        inMemoryHistoryManager.addTask(epics.get(id));
        return epics.get(id);
    }

    @Override
    public Subtask getSubtaskById(int id) {
        inMemoryHistoryManager.addTask(subtasks.get(id));
        return subtasks.get(id);
    }

    @Override
    public void removeTaskById(int taskId) {
        tasks.remove(taskId);
        inMemoryHistoryManager.remove(taskId);
    }

    @Override
    public void removeEpicById(int epicId) {
        epics.remove(epicId);
        inMemoryHistoryManager.remove(epicId);

        ArrayList<Integer> subIdsToRemove = new ArrayList<>();
        for (Subtask subtask : subtasks.values()) {
            if (subtask.getEpicId() == epicId) {
                subIdsToRemove.add(subtask.getId());
            }
        }
        for (Integer id : subIdsToRemove) {
            subtasks.remove(id);
            inMemoryHistoryManager.remove(id);
        }
    }

   @Override
    public void removeSubtaskById(int subtaskId) {
        int neededEpicId = subtasks.get(subtaskId).getEpicId();
        Epic epic = epics.get(neededEpicId);

        subtasks.remove(subtaskId);
        inMemoryHistoryManager.remove(subtaskId);
        epic.getSubtaskIds().remove(Integer.valueOf(subtaskId));
        updateEpicStatus(neededEpicId);
    }

    @Override
    public void removeAllTasks() {
        for (Integer taskId : tasks.keySet()) {
            inMemoryHistoryManager.remove(taskId);
        }
        tasks.clear();
    }

    @Override
    public void removeAllEpics() {
        for (Integer epicId : epics.keySet()) {
            inMemoryHistoryManager.remove(epicId);
        }
        for (Integer subtaskId : subtasks.keySet()) {
            inMemoryHistoryManager.remove(subtaskId);
        }
        epics.clear();
        subtasks.clear();
    }

    @Override
    public void removeAllSubtasks() {
        for (Integer subtaskId : subtasks.keySet()) {
            inMemoryHistoryManager.remove(subtaskId);
        }
        subtasks.clear();
        for (Epic epic : epics.values()) {
            epic.getSubtaskIds().clear();
            epic.setStatus(Status.NEW);
        }
    }

    @Override
    public ArrayList<Task> getAllTasks() {
        ArrayList<Task> allTasks = new ArrayList<>();
        for (Integer taskId : tasks.keySet()) {
            Task task = tasks.get(taskId);
            allTasks.add(task);
        }
        return allTasks;
    }

    @Override
    public ArrayList<Epic> getAllEpics() {
        ArrayList<Epic> allEpics = new ArrayList<>();
        for (Integer epicId : epics.keySet()) {
            Epic epic = epics.get(epicId);
            allEpics.add(epic);
        }
        return allEpics;
    }

    @Override
    public ArrayList<Subtask>getAllSubtasks(){
        ArrayList<Subtask> allSubtasks = new ArrayList<>();
        for (Integer subtaskId : subtasks.keySet()) {
            Subtask subtask = subtasks.get(subtaskId);
            allSubtasks.add(subtask);
        }
        return allSubtasks;
    }

    @Override
    public ArrayList<Subtask> getSubtasksToEpic(int epicId) {
        ArrayList<Subtask> allSubtasksToEpic = new ArrayList<>();
        for (Subtask subtask : subtasks.values()) {
            if (subtask.getEpicId() == epicId) {
                allSubtasksToEpic.add(subtask);
            }
        }
        return allSubtasksToEpic;
    }

    public void updateEpicStatus(int epicId) {

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
                    countNew = 0;
                    countDone = 0;
            }
        }

        if (countNew == epic.getSubtaskIds().size()) {
            epic.setStatus(Status.NEW);
        } else if (countDone == epic.getSubtaskIds().size()) {
            epic.setStatus(Status.DONE);
        } else {
            epic.setStatus(Status.IN_PROGRESS);
        }
    }

}

