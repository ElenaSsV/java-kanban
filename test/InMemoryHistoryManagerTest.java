import TaskTracker.model.Status;
import TaskTracker.model.Task;
import TaskTracker.service.HistoryManager;
import TaskTracker.service.Managers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class InMemoryHistoryManagerTest {
    private HistoryManager historyManager;
    private Task task1;
    private Task task2;
    private Task task3;

    @BeforeEach
    public void beforeEach() {
        historyManager = Managers.getDefaultHistory();
        task1 = new Task("Test Task1", "Test Task1 description", Status.NEW,
                LocalDateTime.of(2023, 6,30, 9,0), 90);
        task1.setId(1);
        task2 = new Task("Test Task2", "Test Task2 Description", Status.NEW,
                LocalDateTime.of(2023, 6,30, 14,0), 90);
        task2.setId(2);
        task3 = new Task("Test Task3", "Test Task3 description", Status.NEW,
                LocalDateTime.of(2023, 6,20, 9,0), 90);
        task3.setId(3);
    }

    @Test
    void addTaskWhenHistoryEmpty() { //also check getHistory method??
        historyManager.addTask(task1);
        final List<Task> history = historyManager.getHistory();
        assertNotNull(history, "История не пустая.");
        assertEquals(1, history.size(), "История не пустая.");
    }

    @Test
    void addDuplicateTask() {
        historyManager.addTask(task1);
        historyManager.addTask(task1);

        final List<Task> history = historyManager.getHistory();
        assertNotNull(history, "История не пустая.");
        assertEquals(1, history.size(), "История не пустая.");
    }

    @Test
    public void removeAtTheBeginning() {
        historyManager.addTask(task1);
        historyManager.addTask(task2);
        historyManager.addTask(task3);
        historyManager.addTask(task1);

        final List<Task> history = historyManager.getHistory();
        assertNotNull(history, "История не пустая.");
        assertEquals(3, history.size(), "История не пустая.");
        assertEquals(task1, history.get(history.size() - 1), "Задача в конце списка не совпадает");
    }

    @Test
    public void removeInTheMiddle() {
        historyManager.addTask(task2);
        historyManager.addTask(task1);
        historyManager.addTask(task3);
        historyManager.addTask(task1);

        final List<Task> history = historyManager.getHistory();
        assertNotNull(history, "История не пустая.");
        assertEquals(3, history.size(), "История не пустая.");
        assertEquals(task1, history.get(history.size() - 1), "Задача в конце списка не совпадает");
    }

    @Test
    public void removeAtTheEnd() {
        historyManager.addTask(task2);
        historyManager.addTask(task3);
        historyManager.addTask(task1);
        historyManager.addTask(task1);

        final List<Task> history = historyManager.getHistory();
        assertNotNull(history, "История не пустая.");
        assertEquals(3, history.size(), "История не пустая.");
        assertEquals(task1, history.get(history.size() - 1), "Задача в конце списка не совпадает");
    }

    @Test
    public void getHistoryIfHistoryEmpty() {
       final List<Task> history = historyManager.getHistory();
        assertTrue(history.isEmpty(), "История не пустая.");
    }
}
