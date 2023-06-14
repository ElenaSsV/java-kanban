import TaskTracker.model.Epic;
import TaskTracker.model.Status;
import TaskTracker.model.Subtask;
import TaskTracker.service.InMemoryTaskManager;
import TaskTracker.service.Managers;
import TaskTracker.service.TaskManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

class EpicTest {
    private TaskManager manager;
    private Epic epic;
    private Subtask subtask1;
    private Subtask subtask2;

    @BeforeEach
    public void beforeEach() {
        manager = new InMemoryTaskManager();
        epic = new Epic("Test epic", "Test epic description");
        final int epicId = manager.createEpic(epic);

        subtask1 = new Subtask("Test Subtask1", "Test  description" ,
                Status.NEW, epicId, LocalDateTime.of(2023, 6,30, 9,0), 90);
        manager.createSubtask(subtask1);
        subtask2 = new Subtask("Test Subtask2", "Test status description" ,
                Status.NEW, epicId, LocalDateTime.of(2023, 6,30, 15,0), 90);
        manager.createSubtask(subtask2);
    }

    @Test
    public void calculateEpicStatusIfAllSubtasksStatusNew() {
        Status epicStatus = epic.getStatus();
        assertEquals(Status.NEW, epicStatus, "У эпика неверный статус");
    }

    @Test
    public void calculateEpicStatusIfNoSubtasks() {
        manager.removeAllSubtasks();
        Status epicStatus = epic.getStatus();
        assertEquals(Status.NEW, epicStatus, "У эпика неверный статус");
    }

    @Test
    public void calculateEpicStatusIfALLSubtasksDone() {
        subtask1.setStatus(Status.DONE);
        manager.updateSubtask(subtask1);
        subtask2.setStatus(Status.DONE);
        manager.updateSubtask(subtask2);

        Status epicStatus = epic.getStatus();
        assertEquals(Status.DONE, epicStatus, "У эпика неверный статус");
    }

    @Test
    public void calculateEpicStatusIfSubtasksDoneAndNew() {
        subtask2.setStatus(Status.DONE);
        manager.updateSubtask(subtask2);

        Status epicStatus = epic.getStatus();
        assertEquals(Status.IN_PROGRESS, epicStatus, "У эпика неверный статус");
    }

    @Test
    public void calculateEpicStatusIfAllSubtasksInProgress() {
        subtask1.setStatus(Status.IN_PROGRESS);
        manager.updateSubtask(subtask1);
        subtask2.setStatus(Status.IN_PROGRESS);
        manager.updateSubtask(subtask2);

        Status epicStatus = epic.getStatus();
        assertEquals(Status.IN_PROGRESS, epicStatus, "У эпика неверный статус");
    }

    @Test
    public void calculateEpicStartAndEndTime() {
        final LocalDateTime expectedEpicStartTime = subtask1.getStartTime();
        final LocalDateTime expectedEpicEndTime = subtask2.getEndTime();
        final LocalDateTime actualEpicStartTime = epic.getStartTime();
        final LocalDateTime actualEpicEndTime = epic.getEndTime();
        final long expectedDuration = subtask1.getDuration() + subtask2.getDuration();
        final long actualDuration = epic.getDuration();

        assertEquals(expectedEpicStartTime, actualEpicStartTime, "Время начала не совпадает");
        assertEquals(expectedEpicEndTime, actualEpicEndTime, "Время окончания не совпадает");
        assertEquals(expectedDuration, actualDuration, "Продолжительность не совпадает");
    }

}