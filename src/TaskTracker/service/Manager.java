package TaskTracker.service;

import TaskTracker.model.Epic;
import TaskTracker.model.Subtask;
import TaskTracker.model.Task;

import java.util.ArrayList;
import java.util.HashMap;


public class Manager {

    private HashMap<Integer, Task> tasks = new HashMap<>();
    private HashMap<Integer, Epic> epics = new HashMap<>();
    private HashMap<Integer, Subtask> subtasks = new HashMap<>();

    private int id = 1;

    public void createTask(Task task) {
        task.setStatus("NEW");
        task.setId(id++);
        tasks.put(task.getId(), task);
    }

    public void createEpic(Epic epic) {
        epic.setStatus("NEW");
        epic.setId(id++);
        epics.put(epic.getId(), epic);
    }

    public void createSubtask(Subtask subtask) {
        subtask.setStatus("NEW");
        subtask.setId(id++);
        subtasks.put(subtask.getId(), subtask);

        int epicId = subtask.getEpicId();

        Epic epic = epics.get(epicId);
        epic.getSubtaskIds().add(subtask.getId());

        updateEpicStatus(epicId);
    }

    public void updateTask(Task task) {
        tasks.put(task.getId(), task);
    }

    public void updateEpic(Epic epic) {
        epics.put(epic.getId(), epic);
    }

    public void updateSubtask(Subtask subtask) {
        subtasks.put(subtask.getId(), subtask);
        updateEpicStatus(subtask.getEpicId());
    }

    public Task getTaskById(int id) {
        return tasks.get(id);
    }

    public Epic getEpicById(int id) {
        return epics.get(id);
    }

    public Subtask getSubtaskById(int id) {
        return subtasks.get(id);
    }

    public void removeTaskById(int taskId) {
        tasks.remove(taskId);
    }

    public void removeEpicById(int epicId) {
        epics.remove(epicId);

        ArrayList<Integer> subIdsToRemove = new ArrayList<>();
        for (Subtask subtask : subtasks.values()) {
            if (subtask.getEpicId() == epicId) {
                subIdsToRemove.add(subtask.getId());
            }
        }
        for (Integer id : subIdsToRemove) {
            subtasks.remove(id);
        }
    }

    public void removeSubtaskById(int subtaskId) {
        int neededEpicId = subtasks.get(subtaskId).getEpicId();
        Epic epic = epics.get(neededEpicId);

        subtasks.remove(subtaskId);
        epic.getSubtaskIds().remove(Integer.valueOf(subtaskId));
        updateEpicStatus(neededEpicId);
    }

    public void removeAllTasks() {
        tasks.clear();
    }

    public void removeAllEpics() {
        epics.clear();
        subtasks.clear();
    }

    public void removeAllSubtasks() {
        subtasks.clear();
        for (Epic epic : epics.values()) {
            epic.getSubtaskIds().clear();
            epic.setStatus("NEW");
        }
    }

    public ArrayList<Task> getAllTasks() {
        ArrayList<Task> allTasks = new ArrayList<>();
        for (Integer taskId : tasks.keySet()) {
            Task task = tasks.get(taskId);
            allTasks.add(task);
        }
        return allTasks;
    }

    public ArrayList<Epic> getAllEpics() {
        ArrayList<Epic> allEpics = new ArrayList<>();
        for (Integer epicId : epics.keySet()) {
            Epic epic = epics.get(epicId);
            allEpics.add(epic);
        }
        return allEpics;
    }

    public ArrayList<Subtask> getAllSubtasks() {
        ArrayList<Subtask> allSubtasks = new ArrayList<>();
        for (Integer subtaskId : subtasks.keySet()) {
            Subtask subtask = subtasks.get(subtaskId);
            allSubtasks.add(subtask);
        }
        return allSubtasks;
    }

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

        String subtaskStatus = "";
        int countNew = 0;
        int countDone = 0;

        Epic epic = epics.get(epicId);

        for (Integer subtaskId : epic.getSubtaskIds()) {
            subtaskStatus = subtasks.get(subtaskId).getStatus();

            switch (subtaskStatus) {
                case "NEW":
                    countNew++;
                        break;
                case "DONE":
                    countDone++;
                       break;
                default:
                    countNew = 0;
                    countDone = 0;
            }
        }

        if (countNew == epic.getSubtaskIds().size()) {
            epic.setStatus("NEW");
        } else if (countDone == epic.getSubtaskIds().size()) {
            epic.setStatus("DONE");
        } else {
            epic.setStatus("IN PROGRESS");
        }
    }
}

