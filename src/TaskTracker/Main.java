package TaskTracker;

import TaskTracker.model.Epic;
import TaskTracker.model.Status;
import TaskTracker.model.Subtask;
import TaskTracker.model.Task;
import TaskTracker.server.KVServer;
import TaskTracker.service.Managers;
import TaskTracker.service.TaskManager;

import java.io.IOException;
import java.time.LocalDateTime;

public class Main {

    public static void main(String[] args) throws IOException, InterruptedException {
        KVServer server = new KVServer();
        server.start();

        TaskManager manager = Managers.getDefault();

        manager.createTask(new Task("Test createTask", "Test createTask description", Status.NEW,
                LocalDateTime.of(2023, 6,30, 10,0), 90));
        manager.createEpic(new Epic("Workshop on 6 April",
                "Organise a workshop for 20 people"));
        manager.createSubtask( new Subtask("Test createSubtask", "Test createSubtask description",
                Status.NEW, 2, LocalDateTime.of(2023, 6,30, 15,30),
                90));


        //TaskManager manager2 = HttpTaskManager.loadFromServer("http://localhost:8087");
        System.out.println(manager.getAllTasks());
        System.out.println(manager.getAllEpics());
        System.out.println(manager.getPrioritizedTasks());
        System.out.println(manager.getHistory());


        server.stop();
//
//      Task task2 = new Task("Test createTask2", "Test createTask description", Status.NEW,
//                LocalDateTime.of(2023, 6,30, 15,0), 90);
//
//      manager.createSubtask( new Subtask("Test createSubtask", "Test createSubtask description",
//                Status.NEW, 1, LocalDateTime.of(2023, 6,30, 10,30),
//                90));
//      manager.createSubtask(new Subtask("Find venue", "Find suitable venue", Status.NEW,
//               1, LocalDateTime.of(2023, 6,30, 15,0), 90));
//      manager.createSubtask(new Subtask("Organise catering", "Find a company", Status.NEW,
//                1, LocalDateTime.of(2023, 6,30, 9,0), 90));
//      Subtask updatedSubtask = new Subtask("Find veryGoodVenue", "Find suitable venue", Status.IN_PROGRESS,
//               1, LocalDateTime.of(2023, 5,30, 9,0), 120);
//      updatedSubtask.setId(2);
//      manager.updateSubtask(updatedSubtask);
//      manager.createEpic(new Epic("Project peach",
//                "Complete project"));
//      System.out.println(manager.getSubtaskById(5));
//
//      System.out.println(manager.getPrioritizedTasks());

    }
}
