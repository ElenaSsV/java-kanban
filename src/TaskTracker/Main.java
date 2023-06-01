package TaskTracker;
import TaskTracker.service.*;

import TaskTracker.model.*;

import java.time.LocalDateTime;

public class Main {

    public static void main(String[] args)  {

        InMemoryTaskManager manager = new InMemoryTaskManager();

        manager.createEpic(new Epic("Workshop on 6 April",
                "Organise a workshop for 20 people"));
        manager.createSubtask(new Subtask("Find venue", "Find suitable venue", Status.NEW,
                1, LocalDateTime.of(2023, 6,2, 9,0), 90));
        manager.createSubtask(new Subtask("Organise catering", "Find a company", Status.NEW,
                1, LocalDateTime.of(2023, 6,30, 9,0), 90));
//       Subtask updatedSubtask = new Subtask("Find veryGoodVenue", "Find suitable venue", Status.IN_PROGRESS,
//               1, LocalDateTime.of(2023, 5,30, 9,0), 120);
//       updatedSubtask.setId(2);
//       manager.updateSubtask(updatedSubtask);
//        manager.createEpic(new Epic("Project peach",
//                "Complete project"));

        System.out.println(manager.getPrioritizedTasks());
//        System.out.println(manager.getAllTasks());
        System.out.println(manager.getAllEpics());
//        manager.removeAllSubtasks();
//        System.out.println(manager.getAllEpics());
    }
}
