package TaskTracker.service;

import TaskTracker.exception.ManagerSaveException;
import TaskTracker.model.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


public class FileBackedTaskManager extends InMemoryTaskManager implements TaskManager {
    private String filename;

    public FileBackedTaskManager(String filename) {
        this.filename = filename;
    }

    public void save()  {
        try (Writer writer = new FileWriter(filename, StandardCharsets.UTF_8)) {
            writer.write("id,type,name,status,description,epic, startTime, duration, endTime\n");
            for (Task task : getAllTasks()) {
                writer.write(toString(task) + "\n");
            }

            for (Epic epic : getAllEpics()) {
                writer.write(toString(epic) + "\n");
            }

            for (Subtask subtask : getAllSubtasks()) {
                writer.write(toString(subtask) + "\n");
            }

            writer.write(" \n");
            writer.write(historyToString(historyManager));
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка записи", e);
        }
    }

    public static FileBackedTaskManager loadFromFile(File file) {
        FileBackedTaskManager manager = new FileBackedTaskManager(file.getName());

        String content = manager.readFileContentsOrNull(file.getPath());
        String[] parts = content.split(" \n");
        String partTasks = parts[0];
        String[] tasks = partTasks.split("\n");
        int maxId = 0;
        Task task = null;

        for (int i = 1; i < tasks.length; i++) {
            task = manager.fromString(tasks[i]);
            manager.addTask(task);
            if (task.getId() > maxId) {
                maxId = task.getId();
            }
        }
        String partsHistory = parts[1];
        if (!partsHistory.isBlank()) {
            List<Integer> viewedTasksIds = historyFromString(partsHistory);
            if (viewedTasksIds.contains(task.getId())) {
                manager.historyManager.addTask(task);
            }
        }

        manager.id = maxId;
        return manager;
    }

    private String readFileContentsOrNull(String path) {
        try {
            return Files.readString(Path.of(path));
        } catch (IOException e) {
            throw new ManagerSaveException("Не удается считать файл", e);
        }
    }

    protected void addTask(Task task) {
        if (task instanceof Epic) {
            Epic epic = (Epic) task;
            epics.put(task.getId(), epic);
        } else if (task instanceof Subtask) {
            Subtask subtask = (Subtask) task;
            subtasks.put(subtask.getId(), subtask);
            prioritizedTasks.add(subtask);
            epics.get(subtask.getEpicId()).getSubtaskIds().add(subtask.getId());
            updateEpicStatus(subtask.getEpicId());
            calculateEpicStartAndEndTime(subtask.getEpicId());
            calculateEpicStartAndEndTime(subtask.getEpicId());
        } else {
            tasks.put(task.getId(), task);
            prioritizedTasks.add(task);
        }
    }

    private String toString(Task task) {
       DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy, HH:mm");
        return task.getId() + "," + getType(task) + "," +  task.getName() + "," + task.getStatus() + ","
                + task.getDescription() + "," + getEpic(task) + "," + task.getStartTime().format(formatter) + ","
                + task.getDuration() + "," + task.getEndTime().format(formatter);
    }

    private Task fromString(String value) {
        String[] elements = value.split(",");
        int id = Integer.parseInt(elements[0]);
        String name = elements[2];
        Status status = Status.valueOf(elements[3]);
        String description = elements[4];

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        LocalDate startDate = LocalDate.parse(elements[6],formatter);

        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern(" HH:mm");
        LocalTime startTime = LocalTime.parse(elements[7], timeFormatter);

        LocalDateTime startDateAndTime = LocalDateTime.of(startDate,startTime);
        long duration = Long.parseLong(elements[8]);

        LocalDate endDate = LocalDate.parse(elements[9], formatter);
        LocalTime endTime = LocalTime.parse(elements[10], timeFormatter);

        LocalDateTime endDateAndTime = LocalDateTime.of(endDate, endTime);

        Type type = Type.valueOf(elements[1]);
        switch (type) {
            case SUBTASK:
                int epicId = Integer.parseInt(elements[5]);
                Subtask subtask = new Subtask(name, description, status, epicId, startDateAndTime, duration);
                subtask.setId(id);
                return subtask;
            case EPIC:
                Epic epic = new Epic(name, description);
                epic.setId(id);
                epic.setStartTime(startDateAndTime);
                epic.setDuration(duration);
                epic.setEndTime(endDateAndTime);
                return epic;
            default:
                Task task = new Task(name, description, status, startDateAndTime, duration);
                task.setId(id);
                return task;
        }
    }

    private static String historyToString(HistoryManager manager) {
        String delimiter = ",";
        String result = "";
        String prefix = "";
        if (manager.getHistory().isEmpty()) {
            result = " ";
        } else {
            for (Task viewedTask : manager.getHistory()) {
                result += prefix + viewedTask.getId();
                prefix = delimiter;
            }
        }
        return result;
    }

    private static List<Integer> historyFromString(String value) {
        String[] elements = value.split(",");
        List<Integer> viewedTasksIds = new ArrayList<>();
        for (String element : elements) {
            viewedTasksIds.add(Integer.parseInt(element));
        }
        return viewedTasksIds;
    }

    private Type getType(Task task) {
       if (task.getClass() == Epic.class) {
           return Type.EPIC;
       } else if (task.getClass() == Subtask.class) {
           return Type.SUBTASK;
       } else {
           return Type.TASK;
       }
    }

    private String getEpic(Task task) {
        String epicId = "";
          if (task.getClass() == Subtask.class) {
              Subtask subtask = (Subtask) task;
              epicId = String.valueOf(subtask.getEpicId());
          }
        return epicId;
    }

    @Override
    public int createTask(Task task)  {
        int taskId = super.createTask(task);
        save();
        return taskId;
    }

    @Override
    public int createEpic(Epic epic) {
       int epicId = super.createEpic(epic);
       save();
       return epicId;
    }

    @Override
    public int createSubtask(Subtask subtask) {
        int subtaskId = super.createSubtask(subtask);
        save();
        return subtaskId;
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        super.updateSubtask(subtask);
        save();
    }

    @Override
    public Optional<Task> getTaskById(int id) {
        Optional<Task> task = super.getTaskById(id);
        save();
        return task;
    }

    @Override
    public Optional<Epic> getEpicById(int id) {
        Optional<Epic> epic = super.getEpicById(id);
        save();
        return epic;
    }

   @Override
   public Optional<Subtask> getSubtaskById(int id) {
       Optional<Subtask> subtask = super.getSubtaskById(id);
        save();
        return subtask;

   }

    @Override
    public void removeTaskById(int id) {
        super.removeTaskById(id);
        save();
    }

    @Override
    public void removeEpicById(int epicId) {
        super.removeEpicById(epicId);
        save();
    }

    @Override
    public void removeSubtaskById(int subtaskId) {
        super.removeSubtaskById(subtaskId);
        save();
    }

    @Override
    public void removeAllTasks() {
        super.removeAllTasks();
        save();
    }

    @Override
    public void removeAllEpics() {
        super.removeAllEpics();
        save();
    }

    @Override
    public void removeAllSubtasks() {
        super.removeAllSubtasks();
        save();
    }

   // public static void main(String[] args) {
//        FileBackedTaskManager manager = new FileBackedTaskManager("backup.csv");
//        InMemoryTaskManager memoryManager = new InMemoryTaskManager();
//
//        manager.createEpic(new Epic("Workshop on 6 April",
//                "Organise a workshop for 20 people"));
//        manager.createSubtask(new Subtask("Find venue",
//                "Find suitable venue", Status.NEW, 1,
//                LocalDateTime.of(2023, 5,30, 9,0), 90));
//        manager.createSubtask(new Subtask("Organise catering", "Find a company", Status.NEW,
//                1, LocalDateTime.of(2023, 5,30, 10,0), 90));
//        manager.createEpic(new Epic("Project peach",
//                "Complete project"));
//        Subtask subtask = new Subtask("Organise catering", "Find a company", Status.DONE,
//                1);
//        subtask.setId(3);
//        manager.updateSubtask(subtask);

//        manager.getEpicById(1);
//        manager.getSubtaskById(3);
//
//        File file = new File ("backup.csv");
//        FileBackedTaskManager fileBackedTaskManager = loadFromFile(file);
////
//        System.out.println(fileBackedTaskManager.getAllEpics());
//        System.out.println(fileBackedTaskManager.getAllSubtasks());
//        System.out.println(fileBackedTaskManager.getHistory());

   // }
}
