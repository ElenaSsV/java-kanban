package TaskTracker.test;

import TaskTracker.model.Status;
import TaskTracker.model.*;
import TaskTracker.service.FileBackedTaskManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class FileBackedTaskManagerTest extends TaskManagerTest<FileBackedTaskManager> {
    @BeforeEach
    public void beforeEach() {
        taskManager = new FileBackedTaskManager("backup.csv");
        epic = new Epic("Test Epic", "Test description");
        task = new Task("Test Task", "Test  description", Status.NEW,
                LocalDateTime.of(2023, 5,30, 9,0), 90);
        subtask = new Subtask("Test Subtask", "Test description",
                Status.NEW, 1, LocalDateTime.of(2023, 5,31, 9,0), 90);
    }

    @Test
    public void addTaskFromFile()  {
        final int taskId = taskManager.createTask(task);

        final Task savedTask = taskManager.getTaskById(taskId);
        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(task, savedTask, "Задачи не совпадают.");

        File file = new File("backup.csv");
        taskManager = FileBackedTaskManager.loadFromFile(file);

        final List<Task> tasks = taskManager.getAllTasks();
        assertFalse(tasks.isEmpty(), "Задачи на возвращаются.");
        assertEquals(1, tasks.size(), "Неверное количество задач.");
        assertEquals(task, tasks.get(0), "Задачи не совпадают.");
    }

    @Test
    public void saveAndLoadFileBackedManager() {
        int epicId = taskManager.createEpic(epic);
        int subtaskId = taskManager.createSubtask(subtask);
        Epic savedEpic = taskManager.getEpicById(epicId);

        File file = new File("backup.csv");
        taskManager = FileBackedTaskManager.loadFromFile(file);

        final Epic restoredEpic = taskManager.getEpicById(epicId);
        assertEquals(savedEpic, restoredEpic, "Эпики не совпадают");
    }

    @Test
    public void saveAndLoadFileBackedManagerIfNoTasks() { // усли список задач пуст
        taskManager.removeAllTasks();
        taskManager.removeAllEpics();

        File file = new File("backup.csv");
        taskManager = FileBackedTaskManager.loadFromFile(file);
        final List<Task> tasks = taskManager.getAllTasks();
        assertTrue(tasks.isEmpty(), "Список задач не пуст");

        final List<Epic> epics = taskManager.getAllEpics();
        assertTrue(epics.isEmpty(), "Список эпиков не пуст");

        final List<Subtask> subtasks = taskManager.getAllSubtasks();
        assertTrue(subtasks.isEmpty(), "Список подзадач не пуст");
    }

    @Test
    public void saveAndLoadFileBackedTaskManagerIfNoHistory() { //если история просмотров пуста
        final int taskId = taskManager.createTask(task);

        File file = new File("backup.csv");
        taskManager = FileBackedTaskManager.loadFromFile(file);
        final List<Task> history = taskManager.getHistory();
        assertTrue(history.isEmpty(), "История просмотров не пуста");
    }

    @Test
    public void saveAndLoadFileBackedTaskManagerIfNoSubtasksToEpic() { //если эпик без подзадач
        final int epicId = taskManager.createEpic(epic);
        final Epic savedEpic = taskManager.getEpicById(epicId);

        File file = new File("backup.csv");
        taskManager = FileBackedTaskManager.loadFromFile(file);

        final Epic restoredEpic = taskManager.getEpicById(epicId);
        assertEquals(savedEpic, restoredEpic, "Эпики не совпадают");

        final List<Integer> subtaskIdsToEpic = restoredEpic.getSubtaskIds();
        assertTrue(subtaskIdsToEpic.isEmpty(), "Список id сабтасков у эпика не пустой");

        Status status = restoredEpic.getStatus();
        assertEquals(Status.NEW, status, "Статусы не совпадают");
    }
}
