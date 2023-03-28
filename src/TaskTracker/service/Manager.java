package TaskTracker.service;

import TaskTracker.model.Epic;
import TaskTracker.model.Subtask;
import TaskTracker.model.Task;

import java.util.ArrayList;
import java.util.HashMap;

public class Manager {

    HashMap<Integer, Task> tasks = new HashMap<>();
    HashMap<Integer, Epic> epics = new HashMap<>();
    HashMap<Integer, Subtask> subtasks = new HashMap<>();

    private int iD = 1;

    public void createTask(Task task) {
        task.setStatus("NEW");
        task.setId(iD++);
        tasks.put(task.getId(), task);
    }

    public void createEpic(Epic epic) {
        epic.setStatus("NEW");
        epic.setId(iD++);
        epics.put(epic.getId(), epic);
    }

    public void createSubtask(Subtask subtask) {
        subtask.setStatus("NEW");
        subtask.setId(iD++);
        subtasks.put(subtask.getId(), subtask);

        for (Epic epic : epics.values()) {
            if (epic.getId() == subtask.getEpicId()) {
                epic.getSubtaskIds().add(subtask.getId());
                return;
            }
        }
    }

    public void updateTask(Task task) {
        tasks.put(task.getId(), task);
    }

    public void updateEpic(Epic epic) {
      epics.put(epic.getId(), epic);
    }

    public void updateSubtask(Subtask subtask) {
        subtasks.put(subtask.getId(), subtask);

        String epicStatus ="";
        int epicId = subtask.getEpicId();
        int count = 0;

        Epic epic = epics.get(epicId);

        for (Integer subtaskId : epic.getSubtaskIds()) {
            Subtask sub = subtasks.get(subtaskId);
            if (sub.getStatus().equals("DONE")) {
                count++;
                if (count == epic.getSubtaskIds().size()) {
                    epicStatus = "DONE";
                } else {
                    epicStatus = "IN PROGRESS";
                }
            }
        }
        epic.setStatus(epicStatus);
    }
    
    public Task getTaskById(int iD) {
        return tasks.get(iD);
    }

    public Epic getEpicById(int iD) {
        return epics.get(iD);
    }

    public Subtask getSubtaskById(int iD) {
        return subtasks.get(iD);
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
        for (Integer iD : subIdsToRemove) {
            subtasks.remove(iD);
        }
    }

    public void removeSubtaskById(int subtaskId) {
        int neededEpicId = 0;

        for (Integer epicId : epics.keySet()) {
            if (epicId == subtasks.get(subtaskId).getEpicId()) {
                neededEpicId = epicId;
            }
        }

        Epic epic = epics.get(neededEpicId);

        for (int i = 0; i < epic.getSubtaskIds().size(); i++) {
            int index = 0;
            int subId = epic.getSubtaskIds().get(i);
            if (subId == subtaskId) {
                index = i;
               epic.getSubtaskIds().remove(index);
               subtasks.remove(subtaskId);
               return;
            }
        }
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
        } return allSubtasksToEpic;
    }
}
