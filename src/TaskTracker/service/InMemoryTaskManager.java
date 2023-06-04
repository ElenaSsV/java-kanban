package TaskTracker.service;

import TaskTracker.exception.TaskValidationException;
import TaskTracker.model.Epic;
import TaskTracker.model.Status;
import TaskTracker.model.Subtask;
import TaskTracker.model.Task;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;


public class InMemoryTaskManager implements TaskManager {

    protected final Map<Integer, Task> tasks = new HashMap<>();
    protected final Map<Integer, Epic> epics = new HashMap<>();
    protected final Map<Integer, Subtask> subtasks = new HashMap<>();
    protected final HistoryManager historyManager = Managers.getDefaultHistory();
    protected final TreeSet<Task> prioritizedTasks = new TreeSet<>(Comparator.comparing(Task::getStartTime));

    protected int id = 1;

    @Override
    public int createTask(Task task)   {
        if (isNotValidTime(task)) {
            throw new TaskValidationException("Ошибка создания задачи");
        }
        task.setId(id++);
        tasks.put(task.getId(), task);
        prioritizedTasks.add(task);
        return task.getId();
    }

    @Override
    public int createEpic(Epic epic) {
        epic.setId(id++);
        epics.put(epic.getId(), epic);
        epic.setDefaultTime(epic.getStartTime());
        return epic.getId();
    }

   @Override
    public int createSubtask(Subtask subtask)  {
        if (!epics.containsKey(subtask.getEpicId()) || isNotValidTime(subtask)) {
            throw new TaskValidationException("Ошибка создания подзадачи");
        }
        int epicId = subtask.getEpicId();
        subtask.setId(id++);
        subtasks.put(subtask.getId(), subtask);
        prioritizedTasks.add(subtask);

        Epic epic = epics.get(epicId);
        epic.getSubtaskIds().add(subtask.getId());
        updateEpicStatus(epicId);
        calculateEpicStartAndEndTime(epicId);

        return subtask.getId();
    }

    @Override
    public void updateTask(Task task) {
        if (!tasks.containsKey(task.getId()) || isNotValidTime(task)) {
            return;
        }

        Task prevVersion = tasks.get(task.getId());
        prioritizedTasks.remove(prevVersion);
        prioritizedTasks.add(task);
        tasks.put(task.getId(), task);
    }

    @Override
    public void updateEpic(Epic epic) {
        if (!epics.containsKey(epic.getId())) {
            return;
        }
        epics.put(epic.getId(), epic);
    }

   @Override
    public void updateSubtask(Subtask subtask)  {
        if (!subtasks.containsKey(subtask.getId()) || isNotValidTime(subtask)) {
            return;
        }
        Subtask prevVersion = subtasks.get(subtask.getId());

        prioritizedTasks.remove(prevVersion);
        prioritizedTasks.add(subtask);

        subtasks.put(subtask.getId(), subtask);
        updateEpicStatus(subtask.getEpicId());
        calculateEpicStartAndEndTime(subtask.getEpicId());
    }

    @Override
    public Optional<Task> getTaskById(int id) {
        Optional<Task> task = Optional.ofNullable(tasks.get(id));
        task.ifPresent(historyManager::addTask);
        return task;
    }

    @Override
    public Optional<Epic> getEpicById(int id) {
        Optional<Epic> epic = Optional.ofNullable(epics.get(id));
        epic.ifPresent(historyManager::addTask);
        return epic;
    }

    @Override
    public Optional<Subtask> getSubtaskById(int id) {
        Optional<Subtask> subtask = Optional.ofNullable(subtasks.get(id));
        subtask.ifPresent(historyManager::addTask);
        return subtask;
    }

    @Override
    public void removeTaskById(int taskId) {
        if (!tasks.containsKey(taskId)) {
            return;
        }
        historyManager.remove(taskId);
        tasks.remove(taskId);
    }

    @Override
    public void removeEpicById(int epicId) {
        if (!epics.containsKey(epicId)) {
            return;
        }
        List<Integer> subIdsToRemove = epics.get(epicId).getSubtaskIds();
        for (Integer id : subIdsToRemove) {
            subtasks.remove(id);
            historyManager.remove(id);
        }
        epics.remove(epicId);
        historyManager.remove(epicId);
    }

   @Override
    public void removeSubtaskById(int subtaskId) {
        if (!subtasks.containsKey(subtaskId)) {
            return;
        }
        int epicId = subtasks.get(subtaskId).getEpicId();

        epics.get(epicId).getSubtaskIds().remove(Integer.valueOf(subtaskId));
        historyManager.remove(subtaskId);
        subtasks.remove(subtaskId);

        updateEpicStatus(epicId);
        calculateEpicStartAndEndTime(epicId);
    }

    @Override
    public void removeAllTasks() {
        if (tasks.isEmpty()) {
            return;
        }
        for (Integer taskId : tasks.keySet()) {
            historyManager.remove(taskId);
        }
        tasks.clear();
    }

    @Override
    public void removeAllEpics() {
        if (epics.isEmpty()) {
            return;
        }
        for (Integer epicId : epics.keySet()) {
            historyManager.remove(epicId);
        }
        for (Integer subtaskId : subtasks.keySet()) {
            historyManager.remove(subtaskId);
        }
        epics.clear();
        if (!subtasks.isEmpty()) {
            subtasks.clear();
        }
    }

    @Override
    public void removeAllSubtasks() {
        if (subtasks.isEmpty()) {
            return;
        }
        for (Integer subtaskId : subtasks.keySet()) {
            historyManager.remove(subtaskId);
        }
        subtasks.clear();
        for (Epic epic : epics.values()) {
            epic.getSubtaskIds().clear();
            epic.setStatus(Status.NEW);
            epic.setStartTime(epic.getDefaultTime());
            epic.setEndTime(epic.getDefaultTime());
            epic.setDuration(0);
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
        if (!epics.containsKey(epicId)) {
            return new ArrayList<>();
        }
        List<Integer> subtaskIds = epics.get(epicId).getSubtaskIds();

        return subtaskIds.stream()
                .map(subtasks::get)
                .collect(Collectors.toList());
    }

    @Override
    public List<Task> getHistory() {
        if (historyManager.getHistory().isEmpty()) {
            return new ArrayList<>();
        } else {
            return historyManager.getHistory();
        }
    }

    @Override
    public TreeSet<Task> getPrioritizedTasks() {
        if (prioritizedTasks.isEmpty()) {
            return new TreeSet<>();
        } else {
            return new TreeSet<>(prioritizedTasks);
        }
    }

    protected void updateEpicStatus(int epicId) {

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

    protected void calculateEpicStartAndEndTime(int epicId) {
        List<Subtask> subtasksToEpic = getSubtasksToEpic(epicId);
        if (subtasksToEpic.isEmpty()) {
            return;
        }
        LocalDateTime startTime = subtasksToEpic.get(0).getStartTime();
        LocalDateTime endTime = subtasksToEpic.get(0).getEndTime();
        long epicDuration = 0;

        for (Subtask subtask : subtasksToEpic) {
            epicDuration += subtask.getDuration();
            if (subtask.getStartTime().isBefore(startTime)) {
                startTime = subtask.getStartTime();
            }
            if (subtask.getEndTime().isAfter(endTime)) {
                endTime = subtask.getEndTime();
            }
        }

        Epic epic = epics.get(epicId);
        epic.setStartTime(startTime);
        epic.setEndTime(endTime);
        epic.setDuration(epicDuration);
    }

    public boolean isNotValidTime(Task task) {
        if (prioritizedTasks.isEmpty()) {
            return false;
        }
       return  prioritizedTasks.stream()
                .filter(prioritizedTask-> prioritizedTask.getId() != task.getId())
                .anyMatch(prioritizedTask -> task.getStartTime().isBefore(prioritizedTask.getEndTime()) &&
                        task.getEndTime().isAfter(prioritizedTask.getStartTime()));
    }
}

