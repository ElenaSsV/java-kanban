package TaskTracker;
import TaskTracker.service.*;

import TaskTracker.model.*;
import java.util.ArrayList;

public class Main {

    public static void main(String[] args) {

        TaskManager manager = Managers.getDefault();
        HistoryManager historyManager = new InMemoryHistoryManager();

        Status status = null;
        int id = 0;

        manager.createTask(new Epic("Workshop on 6 April",
                "Organise a workshop for 20 people", id, status, new ArrayList<>()));

        manager.createTask(new Subtask("Find venue",
                "Find suitable venue", id, status, 1));
        manager.createTask(new Subtask("Organise catering", "Find a company", id, status,
                1));

        System.out.println(manager.getAllEpics());
//        manager.updateTask(new Subtask("Organise catering", "Find a company", 3,
//                 Status.IN_PROGRESS, 1));
//        manager.updateSubtask(new Subtask("Find venue", "Find suitable venue", 2, Status.DONE, 1));
//
        manager.getSubtaskById(2);
        manager.getEpicById(1);
        manager.getEpicById(1);
        manager.getEpicById(1);
        manager.getEpicById(1);
        manager.getEpicById(1);
        manager.getEpicById(1);
        manager.getEpicById(1);
        manager.getEpicById(1);
        manager.getEpicById(1);
        System.out.println(historyManager.getHistory());
        manager.getSubtaskById(3);
        manager.getSubtaskById(2);
        System.out.println(historyManager.getHistory());
    }
}
