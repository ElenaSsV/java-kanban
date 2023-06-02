import TaskTracker.model.Epic;
import TaskTracker.model.Status;
import TaskTracker.model.Subtask;
import TaskTracker.model.Task;
import TaskTracker.service.FileBackedTaskManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class FileBackedTaskManagerTest extends TaskManagerTest<FileBackedTaskManager> {
    @BeforeEach
    public void beforeEach() {
        taskManager = new FileBackedTaskManager("backup.csv");
        epic = new Epic("Test Epic", "Test description");
        task = new Task("Test createTask", "Test createTask description", Status.NEW,
                LocalDateTime.of(2023, 6,30, 10,0), 90);
        subtask = new Subtask("Test Subtask", "Test description",
                Status.NEW, 1, LocalDateTime.of(2023, 6,15, 10,0), 90);
    }

    @Test
    public void addTaskFromFile()  {
        final int taskId = taskManager.createTask(task);

        final Optional<Task> savedTask = taskManager.getTaskById(taskId);
        assertTrue(savedTask.isPresent(), "Задача не найдена.");
        assertEquals(task, savedTask.get(), "Задачи не совпадают.");

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
        Optional<Epic> savedEpic = taskManager.getEpicById(epicId);
        assertTrue(savedEpic.isPresent());

        File file = new File("backup.csv");
        taskManager = FileBackedTaskManager.loadFromFile(file);

        final Optional<Epic> restoredEpic = taskManager.getEpicById(epicId);
        assertTrue(restoredEpic.isPresent());
        assertEquals(savedEpic.get(), restoredEpic.get(), "Эпики не совпадают");
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
        final Optional<Epic> savedEpic = taskManager.getEpicById(epicId);
        assertTrue(savedEpic.isPresent());

        File file = new File("backup.csv");
        taskManager = FileBackedTaskManager.loadFromFile(file);

        final Optional<Epic> restoredEpic = taskManager.getEpicById(epicId);
        assertTrue(restoredEpic.isPresent());
        assertEquals(savedEpic.get(), restoredEpic.get(), "Эпики не совпадают");

        final List<Integer> subtaskIdsToEpic = restoredEpic.get().getSubtaskIds();
        assertTrue(subtaskIdsToEpic.isEmpty(), "Список id сабтасков у эпика не пустой");

        Status status = restoredEpic.get().getStatus();
        assertEquals(Status.NEW, status, "Статусы не совпадают");
    }
}
