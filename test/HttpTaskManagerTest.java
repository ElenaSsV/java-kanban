import TaskTracker.model.Epic;
import TaskTracker.model.Status;
import TaskTracker.model.Subtask;
import TaskTracker.model.Task;
import TaskTracker.server.KVServer;
import TaskTracker.service.HttpTaskManager;
import TaskTracker.service.TaskManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.TreeSet;

import static org.junit.jupiter.api.Assertions.*;

public class HttpTaskManagerTest extends TaskManagerTest <HttpTaskManager> {
    private KVServer server;

    @BeforeEach
    public void beforeEach() throws IOException, InterruptedException {
        server = new KVServer();
        server.start();
        taskManager = new HttpTaskManager("http://localhost:8087");
        epic = new Epic("Test Epic", "Test description");
        task = new Task("Test createTask", "Test createTask description", Status.NEW,
                LocalDateTime.of(2023, 6,30, 10,0), 90);
        subtask = new Subtask("Test Subtask", "Test description",
                Status.NEW, 1, LocalDateTime.of(2023, 6,15, 10,0), 90);
    }

    @AfterEach
    public void afterEach() {
        server.stop();
    }

    @Test
    public void saveAndLoadFromServer() throws IOException, InterruptedException {
        taskManager.createTask(task);

        TaskManager newManager = HttpTaskManager.loadFromServer("http://localhost:8087");
        Optional<Task> savedTask = newManager.getTaskById(1);
        assertTrue(savedTask.isPresent());
        assertEquals(task, savedTask.get(), "Задачи не совпадают");
    }

    @Test
    public void loadIfNoTasks() throws IOException, InterruptedException {
        TaskManager newManager = HttpTaskManager.loadFromServer("http://localhost:8087");
        List<Task> tasks = newManager.getAllTasks();
        assertTrue(tasks.isEmpty());
    }

    @Test
    public void saveAndLoadHistory () {
        int taskId = taskManager.createTask(task);
        taskManager.getTaskById(taskId);

        List<Task> history = taskManager.getHistory();
        assertFalse(history.isEmpty());
        assertEquals(task, history.get(0));
    }

    @Test
    public void saveAndLoadPrioritizedTasks() throws IOException, InterruptedException {
        taskManager.createEpic(epic);
        taskManager.createTask(task);
        taskManager.createSubtask(subtask);

        TaskManager newManager = HttpTaskManager.loadFromServer("http://localhost:8087");
        TreeSet<Task> priority = newManager.getPrioritizedTasks();
        assertFalse(priority.isEmpty());
        System.out.println(priority);
    }
}
