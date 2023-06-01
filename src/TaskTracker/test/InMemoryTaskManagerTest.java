package TaskTracker.test;

import TaskTracker.model.Epic;
import TaskTracker.model.Status;
import TaskTracker.model.Subtask;
import TaskTracker.model.Task;
import TaskTracker.service.InMemoryTaskManager;
import org.junit.jupiter.api.BeforeEach;

import java.time.LocalDateTime;

public class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {
    @BeforeEach
    public void beforeEach() {
        taskManager = new InMemoryTaskManager();
        epic = new Epic("Test Epic", "Test createEpic description");
        task = new Task("Test createTask", "Test createTask description", Status.NEW,
                LocalDateTime.of(2023, 5,30, 9,0), 90);
        subtask = new Subtask("Test createSubtask", "Test createSubtask description",
                Status.NEW, 1, LocalDateTime.of(2023, 6,30, 9,0),
                90);
    }

}
