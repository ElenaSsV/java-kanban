import TaskTracker.exception.TaskValidationException;
import TaskTracker.model.Epic;
import TaskTracker.model.Status;
import TaskTracker.model.Subtask;
import TaskTracker.model.Task;
import TaskTracker.service.TaskManager;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.TreeSet;

import static org.junit.jupiter.api.Assertions.*;

public abstract class TaskManagerTest<T extends TaskManager> {

   protected static TaskManager taskManager;
   protected Epic epic;
   protected Task task;
   protected Subtask subtask;

    @Test
    void createTask()  {
        final int taskId = taskManager.createTask(task);

        final Optional<Task> savedTask = taskManager.getTaskById(taskId);

        assertTrue(savedTask.isPresent(), "Задача не найдена.");
        assertEquals(task, savedTask.get(), "Задачи не совпадают.");

        LocalDateTime expectedEndTime = task.getStartTime().plusMinutes(task.getDuration());
        LocalDateTime actualEndTime = task.getEndTime();

        assertEquals(expectedEndTime, actualEndTime, "Время окончания не совпадает");

        final List<Task> tasks = taskManager.getAllTasks();

        assertNotNull(tasks, "Задачи на возвращаются.");
        assertEquals(1, tasks.size(), "Неверное количество задач.");
        assertEquals(task, tasks.get(0), "Задачи не совпадают.");
    }

    @Test
    public void createTaskShouldThrowExceptionWhenTimeIsNotValid() {
        int taskId = taskManager.createTask(task); //startTime: 30.06.2023, 10:00, endTime: 30.06.2023, 11:30
        Task overLappingTask = new Task("Test createTaskWithSameStartTime",
                "Test createTask description", Status.NEW, LocalDateTime.of(2023, 6,
                30, 10,30), 90);

        final TaskValidationException exception = assertThrows(
                TaskValidationException.class,
                new Executable() {
                    @Override
                    public void execute() throws Throwable {
                        taskManager.createTask(overLappingTask);
                    }
                });
    }

    @Test
    void createEpic() {
        final int epicId = taskManager.createEpic(epic);
        final Optional<Epic> savedEpic = taskManager.getEpicById(epicId);

        assertTrue(savedEpic.isPresent(), "Эпик не найден.");
        assertEquals(epic, savedEpic.get(), "Эпики не совпадают.");

        final List<Epic> epics = taskManager.getAllEpics();

        assertNotNull(epics, "Эпики не возвращаются.");
        assertEquals(1, epics.size(), "Неверное количество задач.");
        assertEquals(epic, epics.get(0), "Задачи не совпадают.");

        Status status = savedEpic.get().getStatus();
        assertEquals(Status.NEW, status, "Статусы не совпадают");
    }

    @Test
    void createSubtask() {
        final int epicId = taskManager.createEpic(epic);
        final int subtaskId = taskManager.createSubtask(subtask);

        final Optional<Subtask> savedSubtask = taskManager.getSubtaskById(subtaskId);
        assertTrue(savedSubtask.isPresent(), "Подзадача не найдена.");
        assertEquals(subtask, savedSubtask.get(), "Подзадачи не совпадают.");

        final List<Subtask> subtasks = taskManager.getAllSubtasks();

        assertNotNull(subtasks, "Подзадачи не возвращаются.");
        assertEquals(1, subtasks.size(), "Неверное количество подзадач.");
        assertEquals(subtask, subtasks.get(0), "Подзадачи не совпадают.");

        final Optional<Epic> epicForSubtask = taskManager.getEpicById(savedSubtask.get().getEpicId());
        assertTrue(epicForSubtask.isPresent(), "Эпик у подзадачи отсутствует");

        final List<Integer> subtaskIdsToEpic = epicForSubtask.get().getSubtaskIds();
        assertNotNull(subtaskIdsToEpic, "Список ID подзадач у эпика пуст.");
        assertEquals(1, subtasks.size(), "Неверное количество подзадач.");
        assertEquals(subtaskId, subtaskIdsToEpic.get(0), "ID подзадачи не совпадает.");
    }

    @Test
    public void createSubtaskShouldThrowExceptionIfNoEpicForSubtask() {
        TaskValidationException exception = assertThrows(
                TaskValidationException.class,
                new Executable() {
                    @Override
                    public void execute() throws Throwable {
                        taskManager.createSubtask(subtask);
                    }
                });
    }

    @Test
    public void createSubtaskShouldThrowExceptionWhenTimeIsNotValid() {
        taskManager.createEpic(epic);
        int subtaskId = taskManager.createSubtask(subtask); //Start: 15.06.2023 9:00, End: 15.06.2023 10:30
        Subtask overLappingSubtask = new Subtask("Test createSubtaskWithSameStartTime",
                "Test createSubtask description", Status.NEW, 1, LocalDateTime.of(2023,
                6,15, 10,0), 90);
        final TaskValidationException exception = assertThrows(
                TaskValidationException.class,
                new Executable() {
                    @Override
                    public void execute() throws Throwable {
                        taskManager.createSubtask(overLappingSubtask);
                    }
                });
    }

    @Test
    void updateTask() {
        final int taskId = taskManager.createTask(task);
        Task updatedTask = new Task("Test updateTask", "Test updateTask description",
                Status.IN_PROGRESS, LocalDateTime.of(2023, 6,20, 10,0), 90);
        updatedTask.setId(taskId);
        taskManager.updateTask(updatedTask);

        final Optional<Task> savedTask = taskManager.getTaskById(taskId);
        assertTrue(savedTask.isPresent(), "Задача не найдена.");
        assertEquals(updatedTask, savedTask.get(), "Задача не обновилась.");

        final List<Task> tasks = taskManager.getAllTasks();

        assertNotNull(tasks, "Задачи не возвращаются.");
        assertEquals(1, tasks.size(), "Неверное количество задач.");
        assertEquals(updatedTask, tasks.get(0), "Задачи не совпадают.");
    }

    @Test
    public void updateEpic() {
        final int epicId = taskManager.createEpic(epic);
        Epic updatedEpic = new Epic("Test updateEpic", "Test updateEpic description");
        updatedEpic.setId(epicId);
        taskManager.updateEpic(updatedEpic);

        final Optional<Epic> savedEpic = taskManager.getEpicById(epicId);
        assertTrue(savedEpic.isPresent(), "Эпик не найден.");
        assertEquals(updatedEpic, savedEpic.get(), "Зпик не обновился.");

        final List<Epic> epics = taskManager.getAllEpics();

        assertNotNull(epics, "Эпики не возвращаются.");
        assertEquals(1, epics.size(), "Неверное количество задач.");
        assertEquals(updatedEpic, epics.get(0), "Задачи не совпадают.");

        Status status = savedEpic.get().getStatus();
        assertEquals(Status.NEW, status, "Статусы не совпадают");
    }
    @Test
    void updateSubtask() {
        final int epicId = taskManager.createEpic(epic);
        final int subtaskId = taskManager.createSubtask(subtask);

        Subtask updatedSubtask = new Subtask("Test updateSubtask", "Test updateSubtask description",
                Status.DONE, epicId, LocalDateTime.of(2023, 7,10, 9,0), 90);
        updatedSubtask.setId(subtaskId);
        taskManager.updateSubtask(updatedSubtask);

        final Optional<Subtask> savedSubtask = taskManager.getSubtaskById(subtaskId);
        assertTrue(savedSubtask.isPresent(), "Подзадача не найдена.");
        assertEquals(updatedSubtask, savedSubtask.get(), "Подзадачи не совпадают.");

        final List<Subtask> subtasks = taskManager.getAllSubtasks();

        assertNotNull(subtasks, "Подзадачи не возвращаются.");
        assertEquals(1, subtasks.size(), "Неверное количество подзадач.");
        assertEquals(updatedSubtask, subtasks.get(0), "Подзадачи не совпадают.");

        final Optional<Epic> epicForSubtask = taskManager.getEpicById(savedSubtask.get().getEpicId()); //проверяем есть ли эпик к которому привязана подзадача
        assertTrue(epicForSubtask.isPresent(), "Эпик у подзадачи отсутствует");

        Status epicStatus = epicForSubtask.get().getStatus();
        assertEquals(Status.DONE, epicStatus, "Статусы не совпадают");

        final List<Integer> subtasksToEpic = epicForSubtask.get().getSubtaskIds();
        assertNotNull(subtasksToEpic, "Список ID подзадач у эпика пуст.");
        assertEquals(1, subtasks.size(), "Неверное количество подзадач.");
        assertEquals(updatedSubtask, subtasks.get(0), "ID подзадачи не совпадает.");
    }

    @Test
    public void subtaskShouldNotBeUpdatedIfOverlapsOtherTask() {
        int epicId = taskManager.createEpic(epic); //1
        int taskId = taskManager.createTask(task); //2
        int subtaskId = taskManager.createSubtask(subtask); //3


        Subtask updatedSubtask = new Subtask("Test updateSubtask", "Test updateSubtask description",
                Status.DONE, epicId, task.getStartTime(), 90); //такое же время как у задачи
        updatedSubtask.setId(subtaskId);
        taskManager.updateSubtask(updatedSubtask);

        Optional<Subtask> savedSubtask = taskManager.getSubtaskById(subtaskId);
        assertEquals(subtask, savedSubtask.get(), "Подзадача обновилась");
        assertNotEquals(updatedSubtask, savedSubtask.get(), "Подзадача обновилась");
    }

    @Test
    public void getTaskById() {
       Optional<Task> noTask = taskManager.getTaskById(1); // если список пуст
       assertTrue(noTask.isEmpty(), "Список задач должен быть пустым");

       final int taskId = taskManager.createTask(task);
       final Optional<Task> savedTask = taskManager.getTaskById(taskId);
       assertTrue(savedTask.isPresent(), "Задача не найдена.");
       assertEquals(task, savedTask.get(), "Задачи не совпадают");

       Optional<Task> nonExistingTask = taskManager.getTaskById(4); //по несуществующему id
       assertTrue(nonExistingTask.isEmpty(), "Задачи с таким id  не существует");
    }

    @Test
    public void getEpicById() {
        Optional<Epic> noEpic = taskManager.getEpicById(1); // если список пуст
        assertTrue(noEpic.isEmpty(), "Список эпиков должен быть пустым");

        final int epicId = taskManager.createEpic(epic);
        final Optional<Epic> savedEpic = taskManager.getEpicById(epicId);
        assertTrue(savedEpic.isPresent(), "Эпик не найден.");
        assertEquals(epic, savedEpic.get(), "Эпики не совпадают");

        final Optional<Epic> nonExistingEpic = taskManager.getEpicById(5); // если неверное id
        assertTrue(nonExistingEpic.isEmpty(), "Эпика с таким id не существует");

        Status status = savedEpic.get().getStatus();
        assertEquals(Status.NEW, status, "Статусы не совпадают");
    }

    @Test
    public void getSubtaskById() {
        Optional<Subtask> noSubtask = taskManager.getSubtaskById(1); //пустой список подзадач
        assertFalse(noSubtask.isPresent(), "Список подзадач должен быть пустым");

        final int epicId = taskManager.createEpic(epic);
        final int subtaskId = taskManager.createSubtask(subtask);
        assertEquals(1, epicId, "У подзадачи неверное  id эпика");


        final Optional<Subtask> savedSubtask = taskManager.getSubtaskById(subtaskId);
        assertTrue(savedSubtask.isPresent(), "Подзадача не найдена.");
        assertEquals(subtask, savedSubtask.get(), "Подзадачи не совпадают");

        final Optional<Epic> epicForSubtask = taskManager.getEpicById(savedSubtask.get().getEpicId()); //проверяем есть ли эпик
        assertTrue(epicForSubtask.isPresent(), "Эпик у подзадачи отсутствует");
    }

    @Test
    public void removeTaskById() {
       // taskManager.removeTaskById(5); //неверное id

        final int taskId = taskManager.createTask(task);
        Optional<Task> savedTask = taskManager.getTaskById(taskId);
        assertTrue(savedTask.isPresent(), "Задачи с таким id нет");

        taskManager.removeTaskById(taskId);

       final List<Task> tasks = taskManager.getAllTasks();
        assertTrue(tasks.isEmpty(), "Задача не удалена");
    }

    @Test
    public void removeEpicById() {
        taskManager.removeEpicById(5); // неверное id
        final int epicId = taskManager.createEpic(epic);

        Optional<Epic> savedEpic = taskManager.getEpicById(epicId);
        assertTrue(savedEpic.isPresent(), "Эпика с таким id нет");

        taskManager.removeEpicById(epicId);

        final List<Epic> epics = taskManager.getAllEpics();
        assertTrue(epics.isEmpty(), "Зпик не удален");

        final List<Subtask> subtasksToEpic = taskManager.getSubtasksToEpic(epicId);
        assertTrue(subtasksToEpic.isEmpty(), "Подзадачи удаленного эпика не удалены");
    }

    @Test
    public void removeSubtaskById() {
        taskManager.removeSubtaskById(5);
        final int epicId = taskManager.createEpic(epic);
        final int subtaskId = taskManager.createSubtask(subtask);

        Optional<Subtask> savedSubtask = taskManager.getSubtaskById(subtaskId);
        assertTrue(savedSubtask.isPresent(), "Подзадачи с таким id нет");

        final Optional<Epic> epicForSubtask = taskManager.getEpicById(savedSubtask.get().getEpicId()); //проверяем есть ли эпик
        assertTrue(epicForSubtask.isPresent(), "Эпик у подзадачи отсутствует");

        taskManager.removeSubtaskById(subtaskId);

        final List<Subtask> subtasks = taskManager.getAllSubtasks();
        assertTrue(subtasks.isEmpty(), "Зпик не удален");


        final List<Integer> subtasksIdsToEpic = epicForSubtask.get().getSubtaskIds();
        assertTrue(subtasksIdsToEpic.isEmpty(), "Id подзадачи не удалилась из списка подазадач у эпика");

        Status epicStatus = epicForSubtask.get().getStatus();
        assertEquals(Status.NEW, epicStatus, "Статус эпика не обновился");
    }

    @Test
    public void removeAllTasks() {
        final int taskId = taskManager.createTask(task);
        Optional<Task> savedTask = taskManager.getTaskById(taskId);
        assertTrue(savedTask.isPresent(), "Задачи с таким id нет");

        taskManager.removeAllTasks();

        final List<Task> tasks = taskManager.getAllTasks();
        assertTrue(tasks.isEmpty(), "Задачи не удалена");
    }

    @Test
    public void removeAllEpics() {
        taskManager.removeAllEpics(); //пустой список
        final int epicId = taskManager.createEpic(epic);
        Optional<Epic> savedEpic = taskManager.getEpicById(epicId);
        assertTrue(savedEpic.isPresent(), "Эпика с таким id нет");

        taskManager.removeAllEpics();

        final List<Epic> epics = taskManager.getAllEpics();
        assertTrue(epics.isEmpty(), "Зпик не удален");

        final List<Subtask> subtasks = taskManager.getAllSubtasks();
        assertTrue(subtasks.isEmpty(), "Подзадачи не удалены");
    }

    @Test
    public void removeAllSubtasks() {
        taskManager.removeAllSubtasks(); // пустой спиок
        final int epicId = taskManager.createEpic(epic);
        final LocalDateTime expectedEpicStartTime = epic.getStartTime();
        final LocalDateTime expectedEndTime = epic.getEndTime();

        final int subtaskId = taskManager.createSubtask(subtask);

        Optional<Subtask> savedSubtask = taskManager.getSubtaskById(subtaskId);
        assertTrue(savedSubtask.isPresent(), "Подзадачи с таким id нет");

        final Optional<Epic> epicForSubtask = taskManager.getEpicById(savedSubtask.get().getEpicId()); //проверяем есть ли эпик
        assertTrue(epicForSubtask.isPresent(), "Эпик у подзадачи отсутствует");

        taskManager.removeAllSubtasks();

        List<Subtask> subtasks = taskManager.getAllSubtasks();
        assertTrue(subtasks.isEmpty(), "Подзадачи не удалены");

        List<Integer> subIdsToEpic = epicForSubtask.get().getSubtaskIds();
        assertTrue(subIdsToEpic.isEmpty(), "У эпика не удалились  id удаленных подзадач");

        LocalDateTime actualEpicStartTime = epic.getStartTime();
        assertEquals(expectedEpicStartTime, actualEpicStartTime, "Время не совпадает");

        LocalDateTime actualEpicEndTime = epic.getEndTime();
        assertEquals(expectedEndTime, actualEpicEndTime, "Время не совпадает");

        assertEquals(0, epic.getDuration(), "Продолжительность не совпадает");
    }

    @Test
    public void getAllTasks() {
        final List<Task> tasksFromEmptyList = taskManager.getAllTasks(); //пустой список задач
        assertTrue(tasksFromEmptyList.isEmpty(), "Список задач не пуст");

        final int taskId = taskManager.createTask(task);
        Optional<Task> savedTask = taskManager.getTaskById(taskId);
        assertTrue(savedTask.isPresent(), "Задачи с таким id нет");

        final List<Task> tasks = taskManager.getAllTasks();
        assertNotNull(tasks, "Задачи на возвращаются.");
        assertEquals(1, tasks.size(), "Неверное количество задач.");
        assertEquals(task, tasks.get(0), "Задачи не совпадают.");
    }

    @Test
    public void getAllEpics() {
        final List<Epic> epicsFromEmptyList = taskManager.getAllEpics(); //пустой список эпиков
        assertTrue(epicsFromEmptyList.isEmpty(), "Список эпиков не пуст");

        final int epicId = taskManager.createEpic(epic);
        Optional<Epic> savedEpic = taskManager.getEpicById(epicId);
        assertTrue(savedEpic.isPresent(), "Эпика с таким id нет");

        final List<Epic> epics = taskManager.getAllEpics();
        assertNotNull(epics, "Эпики не возвращаются.");
        assertEquals(1, epics.size(), "Неверное количество задач.");
        assertEquals(epic, epics.get(0), "Задачи не совпадают.");
    }

    @Test
    public void getAllSubtasks() {
        final List<Subtask> subtasksFromEmptyList = taskManager.getAllSubtasks(); //пустой список подзадач
        assertTrue(subtasksFromEmptyList.isEmpty(), "Списрк подзадач не пуст");
        final int epicId = taskManager.createEpic(epic);

        final int subtaskId = taskManager.createSubtask(subtask);
        Optional<Subtask> savedSubtask = taskManager.getSubtaskById(subtaskId);
        assertTrue(savedSubtask.isPresent(), "Подзадачи с таким id нет");

        final Optional<Epic> epicForSubtask = taskManager.getEpicById(savedSubtask.get().getEpicId()); //проверяем есть ли эпик
        assertTrue(epicForSubtask.isPresent(), "Эпик у подзадачи отсутствует");

        final List<Subtask> subtasks = taskManager.getAllSubtasks();
        assertNotNull(subtasks, "Подзадачи не возвращаются.");
        assertEquals(1, subtasks.size(), "Неверное количество подзадач.");
        assertEquals(subtask, subtasks.get(0), "Подзадачи не совпадают.");
    }

    @Test
    public void getSubtasksToEpic() {
        final int epicId = taskManager.createEpic(epic);
        final List<Subtask> subtasksFromEmptyList = taskManager.getSubtasksToEpic(epicId); //список подзадач пуст
        assertTrue(subtasksFromEmptyList.isEmpty(), "Список подзадач не пуст");

        final int subtaskId = taskManager.createSubtask(subtask);

        Optional<Subtask> savedSubtask = taskManager.getSubtaskById(subtaskId);
        assertTrue(savedSubtask.isPresent(), "Подзадачи с таким id нет");

        final List<Subtask> subtasksToEpic = taskManager.getSubtasksToEpic(epicId);
        assertNotNull(subtasksToEpic, "Список подзадач пуст");
        assertEquals(savedSubtask.get(), subtasksToEpic.get(0), "Подзадачи не совпадают");
    }

    @Test
    public void getHistory()  {
        final int taskId = taskManager.createTask(task);
        taskManager.getTaskById(taskId);

        final List<Task> history = taskManager.getHistory();
        assertNotNull(history, "Список просмотренных задач пуст");
        assertEquals(task, history.get(0), "Просмотренная задача не совпадает");
    }

    @Test
    public void getPrioritizedTasks() {
        int epicId = taskManager.createEpic(epic);
        int taskId = taskManager.createTask(task); //30/06/2023 10:00
        int subtaskId = taskManager.createSubtask(subtask);//15/06/2023 9:00

        TreeSet<Task> prioritizedTasks = taskManager.getPrioritizedTasks();
        assertFalse(prioritizedTasks.isEmpty(), "Список задач пуст");
        assertEquals(subtask, prioritizedTasks.first());
    }
}




