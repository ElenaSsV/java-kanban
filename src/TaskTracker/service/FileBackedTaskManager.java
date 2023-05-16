package TaskTracker.service;
import TaskTracker.model.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;


public class FileBackedTaskManager extends InMemoryTaskManager implements TaskManager {
    String filename;

    public FileBackedTaskManager(String filename) {
        this.filename = filename;
    }

    public void save()  {
        try (Writer writer = new FileWriter(filename, StandardCharsets.UTF_8)) {
            writer.write("id,type,name,status,description,epic\n");
            for (Task task : getAllTasks()) {
                writer.write(toString(task) + "\n");
            }

            for (Epic epic : getAllEpics()) {
                writer.write(toString(epic) + "\n");
            }

            for (Subtask subtask : getAllSubtasks()) {
                writer.write(toString(subtask) + "\n");
            }

            writer.write("\n");
            writer.write(historyToString(getHistoryManager()));
        } catch (IOException e) {
            throw new ManagerSaveException();
        }
    }

    public static FileBackedTaskManager loadFromFile(File file) {
        FileBackedTaskManager manager = new FileBackedTaskManager(file.getName());

        String content = manager.readFileContentsOrNull(file.getPath());
        String[] lines = content.split("\n");

        for (int i = 1; i < lines.length-2; i++) {
            Task task = manager.fromString(lines[i]);
            manager.addTaskFromFile(task);

            List<Integer> viewedTasksIds = historyFromString(lines[lines.length-1]);
            if (viewedTasksIds.contains(task.getId())) {
                manager.getHistoryManager().addTask(task);
            }
        }
        return manager;
    }

    private String readFileContentsOrNull(String path) {
        try {
            return Files.readString(Path.of(path));
        } catch (IOException e) {
            System.out.println("Невозможно прочитать файл.");
            return null;
        }
    }

    public String toString(Task task) {
        return task.getId() + "," + getType(task) + "," +  task.getName() + "," + task.getStatus() + ","
                + task.getDescription() + "," + getEpic(task);
    }

    public Task fromString(String value) {
        String[] elements = value.split(",");
        int id = Integer.parseInt(elements[0]);
        String name = elements[2];
        Status status = Status.valueOf(elements[3]);
        String description = elements[4];

        Type type = Type.valueOf(elements[1]);
        switch (type) {
            case SUBTASK:
                int epicId = Integer.parseInt(elements[5]);
                Subtask subtask = new Subtask(name, description, status, epicId);
                subtask.setId(id);
                return subtask;
            case EPIC:
                Epic epic = new Epic(name, description);
                epic.setId(id);
                return epic;
            default:
                Task task = new Task(name, description, status);
                task.setId(id);
                return task;
        }
    }

    public static String historyToString(HistoryManager manager) {
        String delimiter = ",";
        String result = "";
        String prefix = "";

        for (Task viewedTask : manager.getHistory()) {
            result += prefix + viewedTask.getId();
            prefix = delimiter;
        }
        return result;
    }

    public static List<Integer> historyFromString(String value) {
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
    public void createTask(Task task) {
        super.createTask(task);
        save();
    }

    @Override
    public void createEpic(Epic epic) {
       super.createEpic(epic);
       save();
    }

    @Override
    public void createSubtask(Subtask subtask) {
        super.createSubtask(subtask);
        save();
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
    public Task getTaskById(int id) {
        return super.getTaskById(id);
    }

    @Override
    public Epic getEpicById(int id) {
        Epic epic = super.getEpicById(id);
        save();
        return epic;
    }

   @Override
   public Subtask getSubtaskById(int id) {
        Subtask subtask = super.getSubtaskById(id);
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

    public static void main(String[] args) {
        FileBackedTaskManager manager = new FileBackedTaskManager("backup.csv");
        InMemoryTaskManager memoryManager = new InMemoryTaskManager();

        manager.createEpic(new Epic("Workshop on 6 April",
                "Organise a workshop for 20 people"));
        manager.createSubtask(new Subtask("Find venue",
                "Find suitable venue", Status.NEW, 1));
        manager.createSubtask(new Subtask("Organise catering", "Find a company", Status.NEW,
                1));
        manager.createEpic(new Epic("Project peach",
                "Complete project"));
        Subtask subtask = new Subtask("Organise catering", "Find a company", Status.DONE,
                1);
        subtask.setId(3);
        manager.updateSubtask(subtask);

        manager.getEpicById(1);
        manager.getSubtaskById(3);

//        File file = new File ("backup.csv");
//        FileBackedTaskManager fileBackedTaskManager = loadFromFile(file);
//
//        System.out.println(fileBackedTaskManager.getAllEpics());
//        System.out.println(fileBackedTaskManager.getHistory());

    }
}
